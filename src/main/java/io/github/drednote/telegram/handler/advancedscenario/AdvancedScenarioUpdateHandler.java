package io.github.drednote.telegram.handler.advancedscenario;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.*;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioManager;
import io.github.drednote.telegram.handler.advancedscenario.core.NextActualState;
import io.github.drednote.telegram.handler.advancedscenario.core.UserScenarioContext;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioFactory;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioStorage;
import io.github.drednote.telegram.handler.advancedscenario.core.models.RequestMappingPair;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;

import java.time.Instant;
import java.util.*;

@BetaApi
@Slf4j
@Order(FilterOrder.HIGHEST_PRECEDENCE)
public class AdvancedScenarioUpdateHandler implements UpdateHandler {

    private final IAdvancedScenarioStorage storage;
    private final IAdvancedActiveScenarioFactory activeScenarioFactory;
    private AdvancedScenarioManager advancedScenarioManager;

    public AdvancedScenarioUpdateHandler(IAdvancedScenarioStorage storage, IAdvancedActiveScenarioFactory activeScenarioFactory) {
        super();
        this.storage = storage;
        this.activeScenarioFactory = activeScenarioFactory;
    }

    @Override
    public void onUpdate(UpdateRequest request) {
        if (request.getAdvancedScenarioManager() != null && !request.getAdvancedScenarioManager().getScenarios().isEmpty()) {
            advancedScenarioManager = request.getAdvancedScenarioManager();
            Optional<IAdvancedScenarioEntity> optionalAdvancedScenarioEntity = this.storage.findById(request.getUserId() + ":" + request.getChatId());
            UserScenarioContext context = new UserScenarioContext(request, optionalAdvancedScenarioEntity.flatMap(IAdvancedScenarioEntity::getData));
            optionalAdvancedScenarioEntity.ifPresent(advancedScenarioEntity1 -> request.getAdvancedScenarioManager().setActiveScenarios(advancedScenarioEntity1.getActiveScenarios()));

            @NotNull List<AdvancedScenario<?>> advancedActiveScenarios = request.getAdvancedScenarioManager().getActiveScenarios();
            for (AdvancedScenario<?> advancedActiveScenario : advancedActiveScenarios) {
                List<RequestMappingPair> requestMappings = new ArrayList<>();
                for (TelegramRequest telegramRequest : advancedActiveScenario.getActiveConditions()) {
                    requestMappings.add(new RequestMappingPair(telegramRequest, fromTelegramRequest(telegramRequest)));
                }
                for (RequestMappingPair requestMappingPair : requestMappings) {
                    if (requestMappingPair.getUpdateRequestMapping().matches(request)) {
                        context.setTelegramRequest(requestMappingPair.getTelegramRequest());
                        NextActualState<?> nextActualState = processOfObtainingNextActState(advancedActiveScenario, context);
                        String scenarioName = request.getAdvancedScenarioManager().findScenarioName(advancedActiveScenario);

                        IAdvancedScenarioEntity advancedScenarioEntity = optionalAdvancedScenarioEntity.orElse(null);

                        if (advancedScenarioEntity != null) {
                            Optional<List<IAdvancedActiveScenarioEntity>> activeScenariosOptional = advancedScenarioEntity.getActiveScenarios();
                            // Create or retrieve the list of active scenarios
                            List<IAdvancedActiveScenarioEntity> activeScenarios = activeScenariosOptional.orElseGet(ArrayList::new);
                            createOrUpdateActiveScenario(activeScenarios, scenarioName, nextActualState);
                        } else {
                            advancedScenarioEntity = activeScenarioFactory.createScenarioEntity(request.getUserId(), request.getChatId(), Instant.now(), Optional.of(List.of(createNewActiveScenario(scenarioName, nextActualState))), Optional.of(context.getData().toString()));
                        }
                        this.storage.save(advancedScenarioEntity);
                    }
                }
            }
        }
        ResponseSetter.setResponse(request, null);
    }

    /**
     * Getting next active state and scenario to save in DB
     *
     * @param advancedActiveScenario
     * @param context
     * @return
     */
    private NextActualState<?> processOfObtainingNextActState(AdvancedScenario<?> advancedActiveScenario, UserScenarioContext context) {
        NextActualState<?> nextActualState = advancedActiveScenario.process(context);
        if (nextActualState.getNextScenario() != null && !Objects.equals(nextActualState.getNextScenario(), advancedActiveScenario.getCurrentScenarioName())) {
            AdvancedScenario<?> nextAdvancedActiveScenario = advancedScenarioManager.findScenarioByName(nextActualState.getNextScenario());
            context.setTelegramRequest(null);
            return nextAdvancedActiveScenario.process(context);
        } else {
            return nextActualState;
        }
    }

    /**
     * Create new active scenario entity for DB saving
     * @param scenarioName
     * @param nextActualState
     * @return
     */
    private IAdvancedActiveScenarioEntity createNewActiveScenario(String scenarioName, NextActualState<?> nextActualState) {
        return activeScenarioFactory.createActiveScenarioEntity(nextActualState.getNextScenario() != null ? nextActualState.getNextScenario() : scenarioName, findStateFromNewScenarioIfNeeded(nextActualState));
    }

    /**
     * Create new or update active scenario entity for DB saving
     * @param activeScenarioEntities
     * @param scenarioName
     * @param nextActualState
     * @return
     */
    private IAdvancedActiveScenarioEntity createOrUpdateActiveScenario(List<IAdvancedActiveScenarioEntity> activeScenarioEntities, String scenarioName, NextActualState<?> nextActualState) {
        IAdvancedActiveScenarioEntity existingActiveScenario = activeScenarioEntities.stream().filter(activeScenarioEntitie -> activeScenarioEntitie.getScenarioName().equals(scenarioName)).findFirst().orElse(null);
        if (existingActiveScenario != null) {
            if (nextActualState.getNextScenario() != null) {
                existingActiveScenario.setScenarioName(nextActualState.getNextScenario());
            }
            existingActiveScenario.setStatusName(findStateFromNewScenarioIfNeeded(nextActualState).toString());
            return existingActiveScenario;
        } else {
            return activeScenarioFactory.createActiveScenarioEntity(nextActualState.getNextScenario() != null ? nextActualState.getNextScenario() : scenarioName, findStateFromNewScenarioIfNeeded(nextActualState));
        }
    }

    private Enum<?> findStateFromNewScenarioIfNeeded(NextActualState<?> nextActualState) {
        if (nextActualState.getScenarioState() == null) {
            return advancedScenarioManager.findScenarioByName(nextActualState.getNextScenario()).getStartState();
        } else {
            return nextActualState.getScenarioState();
        }
    }


    private static UpdateRequestMapping fromTelegramRequest(TelegramRequest request) {
        String pattern = request.getPatterns().stream().findFirst().orElse(null);
        RequestType requestType = request.getRequestTypes().stream().findFirst().orElse(RequestType.MESSAGE);
        MessageType messageType = request.getMessageTypes().stream().findFirst().orElse(MessageType.COMMAND);

        // Create a Set with a single element if messageType is not null, otherwise an empty Set
        Set<MessageType> messageTypes = messageType != null ? Set.of(messageType) : Collections.emptySet();

        return new UpdateRequestMapping(pattern, requestType, messageTypes, request.exclusiveMessageType());
    }

}
