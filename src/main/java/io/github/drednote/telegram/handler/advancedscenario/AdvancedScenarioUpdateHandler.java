package io.github.drednote.telegram.handler.advancedscenario;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.*;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.UserScenarioContext;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioFactory;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioStorage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;

import java.util.*;

@BetaApi
@Slf4j
@Order(FilterOrder.HIGHEST_PRECEDENCE)
public class AdvancedScenarioUpdateHandler implements UpdateHandler {

    private final IAdvancedScenarioStorage storage;
    private final IAdvancedActiveScenarioFactory activeScenarioFactory;

    public AdvancedScenarioUpdateHandler(IAdvancedScenarioStorage storage, IAdvancedActiveScenarioFactory activeScenarioFactory) {
        super();
        this.storage = storage;
        this.activeScenarioFactory = activeScenarioFactory;
    }

    @Override
    public void onUpdate(UpdateRequest request) {
        if (request.getAdvancedScenarioManager() != null && !request.getAdvancedScenarioManager().getScenarios().isEmpty()) {
            Optional<IAdvancedScenarioEntity> optionalAdvancedScenarioEntity = this.storage.findById(request.getUserId() + ":" + request.getChatId());
            UserScenarioContext context = new UserScenarioContext(request, optionalAdvancedScenarioEntity.map(IAdvancedScenarioEntity::getData).orElse(null));
            optionalAdvancedScenarioEntity.ifPresent(advancedScenarioEntity1 -> request.getAdvancedScenarioManager().setActiveScenarios(advancedScenarioEntity1.getActiveScenarios()));

            @NotNull List<AdvancedScenario<?>> advancedActiveScenarios = request.getAdvancedScenarioManager().getActiveScenarios();
            for (AdvancedScenario<?> advancedActiveScenario : advancedActiveScenarios) {
                for (UpdateRequestMapping handlerMethod : advancedActiveScenario.getActiveConditions().stream().map(AdvancedScenarioUpdateHandler::fromTelegramRequest).toList()) {
                    if (handlerMethod.matches(request)) {
                        Enum<?> status = advancedActiveScenario.process(context);
                        String scenarioName = request.getAdvancedScenarioManager().findScenarioName(advancedActiveScenario);
                        System.out.println(scenarioName + " - " + status);
                        optionalAdvancedScenarioEntity.ifPresent(advancedScenarioEntity -> {
                            // Get the current list of active scenarios
                            Optional<List<IAdvancedActiveScenarioEntity>> activeScenariosOptional = advancedScenarioEntity.getActiveScenarios();

                            // Create or retrieve the list of active scenarios
                            List<IAdvancedActiveScenarioEntity> activeScenarios = activeScenariosOptional.orElseGet(ArrayList::new);

                            // Create a new active scenario
                            IAdvancedActiveScenarioEntity newActiveScenario = createNewActiveScenario(scenarioName, status);

                            // Add the new scenario to the list
                            activeScenarios.add(newActiveScenario);

                            // Update the list of active scenarios in the entity
                            updateActiveScenarios(advancedScenarioEntity, activeScenarios);
                            this.storage.save(advancedScenarioEntity);
                        });

                    }
                }
            }
        }
        ResponseSetter.setResponse(request, null);
    }

    private IAdvancedActiveScenarioEntity createNewActiveScenario(String scenarioName, Enum<?> status) {
        IAdvancedActiveScenarioEntity newActiveScenario = activeScenarioFactory.create(scenarioName, status);
        return newActiveScenario;
    }

    private void updateActiveScenarios(IAdvancedScenarioEntity advancedScenarioEntity, List<IAdvancedActiveScenarioEntity> activeScenarios) {
        advancedScenarioEntity.setActiveScenarios(Optional.of(activeScenarios));
    }

    private static UpdateRequestMapping fromTelegramRequest(@NonNull TelegramRequest request) {
        String pattern = request.getPatterns().stream().findFirst().orElse(null);
        RequestType requestType = request.getRequestTypes().stream().findFirst().orElse(null);
        MessageType messageType = request.getMessageTypes().stream().findFirst().orElse(null);

        // Create a Set with a single element if messageType is not null, otherwise an empty Set
        Set<MessageType> messageTypes = messageType != null ? Set.of(messageType) : Collections.emptySet();

        return new UpdateRequestMapping(pattern, requestType, messageTypes, request.exclusiveMessageType());
    }

}
