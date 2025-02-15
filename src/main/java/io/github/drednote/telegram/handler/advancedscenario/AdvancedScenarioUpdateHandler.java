package io.github.drednote.telegram.handler.advancedscenario;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            UserScenarioContext context = new UserScenarioContext(request, optionalAdvancedScenarioEntity.map(IAdvancedScenarioEntity::getData).orElse(null));

            @NotNull List<AdvancedScenario<?>> advancedActiveScenarios = request.getAdvancedScenarioManager().getScenarios();
            for (AdvancedScenario<?> advancedActiveScenario : advancedActiveScenarios) {
                List<RequestMappingPair> requestMappings = fetchRequestMappings(advancedActiveScenario, optionalAdvancedScenarioEntity);
                for (RequestMappingPair requestMappingPair : requestMappings) {
                    if (requestMappingPair.getUpdateRequestMapping().matches(request)) {
                        context.setTelegramRequest(requestMappingPair.getTelegramRequest());
                        NextActualState<?> nextActualState = processOfObtainingNextActState(advancedActiveScenario, context, optionalAdvancedScenarioEntity);
                        String scenarioName = request.getAdvancedScenarioManager().findScenarioName(advancedActiveScenario);

                        IAdvancedScenarioEntity advancedScenarioEntity = optionalAdvancedScenarioEntity.orElse(null);

                        if (advancedScenarioEntity != null) {
                            // If the stage of the scenario is finished. deleting active scenario form DB
                            if (Boolean.TRUE.equals(context.getIsFinished())) {
                                advancedScenarioEntity.removeActiveScenarioByName(scenarioName);
                                // If active scenarios are empty no need to store the whole entity in DB
                                if (advancedScenarioEntity.getActiveScenarios() == null || advancedScenarioEntity.getActiveScenarios().isEmpty()) {
                                    this.storage.deleteById(advancedScenarioEntity.getKey());
                                    return;
                                }
                            }

                            List<IAdvancedActiveScenarioEntity> activeScenarios =
                                    Optional.ofNullable(advancedScenarioEntity.getActiveScenarios()).orElseGet(ArrayList::new);
                            List<IAdvancedActiveScenarioEntity> activeScenariosReturn =
                                    createOrUpdateActiveScenario(activeScenarios, scenarioName, nextActualState);
                            Optional.ofNullable(activeScenariosReturn).ifPresent(advancedScenarioEntity::setActiveScenarios);
                        } else {
                            advancedScenarioEntity = activeScenarioFactory.createScenarioEntity(request.getUserId(), request.getChatId(), Instant.now(), List.of(createNewActiveScenario(scenarioName, nextActualState)), context.getData() == null ? null : context.getData().toString());
                        }
                        this.storage.save(advancedScenarioEntity);

                    }
                }
            }
        }

    }

    private List<RequestMappingPair> fetchRequestMappings(AdvancedScenario<?> advancedActiveScenario, Optional<IAdvancedScenarioEntity> optionalAdvancedScenarioEntity) {
        // Retrieve the optional scenario entity from storage using the composite key (userId:chatId)
        return optionalAdvancedScenarioEntity
                .flatMap(entity -> entity.findActiveScenarioByName(advancedActiveScenario.getCurrentScenarioName()))
                .map(activeScenario -> {
                    String currentState = activeScenario.getStatusName();
                    return advancedActiveScenario.getActiveConditions(currentState)
                            .stream()
                            .map(telegramRequest -> new RequestMappingPair(telegramRequest, fromTelegramRequest(telegramRequest)))
                            .toList();
                })
                .orElseGet(() ->
                        advancedActiveScenario.getActiveConditions(null)
                                .stream()
                                .map(telegramRequest -> new RequestMappingPair(telegramRequest, fromTelegramRequest(telegramRequest)))
                                .toList()
                );
    }


    /**
     * Getting next active state and scenario to save in DB
     *
     * @param advancedActiveScenario
     * @param context
     * @return
     */
    private NextActualState<?> processOfObtainingNextActState(AdvancedScenario<?> advancedActiveScenario, UserScenarioContext context, Optional<IAdvancedScenarioEntity> optionalAdvancedScenarioEntity) {
        return optionalAdvancedScenarioEntity
                .flatMap(advancedScenarioEntity -> {
                    // Find the active scenario by name
                    return advancedScenarioEntity.findActiveScenarioByName(advancedActiveScenario.getCurrentScenarioName())
                            .flatMap(advancedActiveScenarioEntity -> {
                                // Process the current scenario
                                NextActualState<?> nextActualState = advancedActiveScenario.process(context, advancedActiveScenarioEntity.getStatusName());

                                // Check if the next scenario differs from the current one
                                if (nextActualState.getNextScenario() != null && !Objects.equals(nextActualState.getNextScenario(), advancedActiveScenario.getCurrentScenarioName())) {
                                    // Load the next scenario and process it
                                    AdvancedScenario<?> nextAdvancedActiveScenario = advancedScenarioManager.findScenarioByName(nextActualState.getNextScenario());
                                    context.setTelegramRequest(null);
                                    return Optional.of(nextAdvancedActiveScenario.process(context, null));
                                } else {
                                    // Return the current next state
                                    return Optional.of(nextActualState);
                                }
                            });
                })
                // Handle the case where optionalAdvancedScenarioEntity is empty
                .orElseGet(() -> {
                    // Simulate processing with null as the scenario name
                    NextActualState<?> fallbackNextActualState = advancedActiveScenario.process(context, null);
                    if (fallbackNextActualState.getNextScenario() != null && !Objects.equals(fallbackNextActualState.getNextScenario(), advancedActiveScenario.getCurrentScenarioName())) {
                        AdvancedScenario<?> nextAdvancedActiveScenario = advancedScenarioManager.findScenarioByName(fallbackNextActualState.getNextScenario());
                        context.setTelegramRequest(null);
                        return nextAdvancedActiveScenario.process(context, null);
                    } else {
                        return fallbackNextActualState;
                    }
                });
    }

    /**
     * Create new active scenario entity for DB saving
     *
     * @param scenarioName
     * @param nextActualState
     * @return
     */
    private IAdvancedActiveScenarioEntity createNewActiveScenario(String scenarioName, NextActualState<?> nextActualState) {
        return activeScenarioFactory.createActiveScenarioEntity(nextActualState.getNextScenario() != null ? nextActualState.getNextScenario() : scenarioName, findStateFromNewScenarioIfNeeded(nextActualState));
    }

    /**
     * Create new or update active scenario entity for DB saving
     *
     * @param activeScenarioEntities
     * @param scenarioName
     * @param nextActualState
     * @return
     */
    private List<IAdvancedActiveScenarioEntity> createOrUpdateActiveScenario(List<IAdvancedActiveScenarioEntity> activeScenarioEntities, String scenarioName, NextActualState<?> nextActualState) {
        IAdvancedActiveScenarioEntity existingActiveScenario = activeScenarioEntities.stream().filter(activeScenarioEntitie -> activeScenarioEntitie.getScenarioName().equals(scenarioName)).findFirst().orElse(null);
        if (existingActiveScenario != null) {
            if (nextActualState.getNextScenario() != null) {
                IAdvancedActiveScenarioEntity existingNextActiveScenario = activeScenarioEntities.stream().filter(activeScenarioEntitie -> activeScenarioEntitie.getScenarioName().equals(nextActualState.getNextScenario())).findFirst().orElse(null);
                if (existingNextActiveScenario != null) {
                    existingNextActiveScenario.setStatusName(findStateFromNewScenarioIfNeeded(nextActualState).toString());
                    return activeScenarioEntities.stream().filter(activeScenarioEntitie -> !activeScenarioEntitie.getScenarioName().equals(existingActiveScenario.getScenarioName())).collect(Collectors.toList());
                } else {
                    existingActiveScenario.setScenarioName(nextActualState.getNextScenario());
                    existingActiveScenario.setStatusName(findStateFromNewScenarioIfNeeded(nextActualState).toString());
                }
            } else {
                existingActiveScenario.setStatusName(findStateFromNewScenarioIfNeeded(nextActualState).toString());
            }

            return null;
        } else {
            return Stream.concat(
                    activeScenarioEntities.stream(),
                    Stream.of(
                            activeScenarioFactory.createActiveScenarioEntity(
                                    nextActualState.getNextScenario() != null ? nextActualState.getNextScenario() : scenarioName,
                                    findStateFromNewScenarioIfNeeded(nextActualState)
                            )
                    )
            ).collect(Collectors.toList());
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
