package io.github.drednote.telegram.handler.advancedscenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.*;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.handler.advancedscenario.core.*;
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
                        ScenarioWithState<?> scenarioWithState = processOfObtainingNextActState(advancedActiveScenario, context, optionalAdvancedScenarioEntity);
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
                                    createOrUpdateActiveScenario(activeScenarios, scenarioName, scenarioWithState);
                            Optional.ofNullable(activeScenariosReturn).ifPresent(advancedScenarioEntity::setActiveScenarios);
                            advancedScenarioEntity.setData(context.getData().isEmpty() ? null : context.getData().toString());
                        } else {
                            advancedScenarioEntity = activeScenarioFactory.createScenarioEntity(request.getUserId(), request.getChatId(), Instant.now(), List.of(createNewActiveScenario(scenarioName, scenarioWithState)), context.getData().isEmpty() ? null : context.getData().toString());
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
    private ScenarioWithState<?> processOfObtainingNextActState(AdvancedScenario<?> advancedActiveScenario, UserScenarioContext context, Optional<IAdvancedScenarioEntity> optionalAdvancedScenarioEntity) {
        return optionalAdvancedScenarioEntity
                .flatMap(advancedScenarioEntity -> {
                    // Find the active scenario by name
                    return advancedScenarioEntity.findActiveScenarioByName(advancedActiveScenario.getCurrentScenarioName())
                            .flatMap(advancedActiveScenarioEntity -> {
                                // Process the current scenario
                                TransitionContext transitionContext = advancedActiveScenario.process(context, null, advancedActiveScenarioEntity.getStatusName());
                                ScenarioWithState<?> scenarioWithState = transitionContext.getNextScenarioWithState();
                                // Check if the next scenario differs from the current one
                                if (scenarioWithState.getNextScenario() != null && !Objects.equals(scenarioWithState.getNextScenario(), advancedActiveScenario.getCurrentScenarioName())) {
                                    // Load the next scenario and process it
                                    AdvancedScenario<?> nextAdvancedActiveScenario = advancedScenarioManager.findScenarioByName(scenarioWithState.getNextScenario());
                                    context.setTelegramRequest(null);
                                    return Optional.of(nextAdvancedActiveScenario.process(context, transitionContext.getPreviosScenarioWithState(), null).getNextScenarioWithState());
                                } else {
                                    // Return the current next state
                                    return Optional.of(scenarioWithState);
                                }
                            });
                })
                // Handle the case where optionalAdvancedScenarioEntity is empty
                .orElseGet(() -> {
                    // Simulate processing with null as the scenario name
                    TransitionContext transitionContext = advancedActiveScenario.process(context, null, null);
                    ScenarioWithState<?> fallbackScenarioWithState = transitionContext.getNextScenarioWithState();
                    if (fallbackScenarioWithState.getNextScenario() != null && !Objects.equals(fallbackScenarioWithState.getNextScenario(), advancedActiveScenario.getCurrentScenarioName())) {
                        AdvancedScenario<?> nextAdvancedActiveScenario = advancedScenarioManager.findScenarioByName(fallbackScenarioWithState.getNextScenario());
                        context.setTelegramRequest(null);
                        return nextAdvancedActiveScenario.process(context, transitionContext.getPreviosScenarioWithState(), null).getNextScenarioWithState();
                    } else {
                        return fallbackScenarioWithState;
                    }
                });
    }

    /**
     * Create new active scenario entity for DB saving
     *
     * @param scenarioName
     * @param scenarioWithState
     * @return
     */
    private IAdvancedActiveScenarioEntity createNewActiveScenario(String scenarioName, ScenarioWithState<?> scenarioWithState) {
        return activeScenarioFactory.createActiveScenarioEntity(scenarioWithState.getNextScenario() != null ? scenarioWithState.getNextScenario() : scenarioName, findStateFromNewScenarioIfNeeded(scenarioWithState));
    }

    /**
     * Create new or update active scenario entity for DB saving
     *
     * @param activeScenarioEntities
     * @param scenarioName
     * @param scenarioWithState
     * @return
     */
    private List<IAdvancedActiveScenarioEntity> createOrUpdateActiveScenario(List<IAdvancedActiveScenarioEntity> activeScenarioEntities, String scenarioName, ScenarioWithState<?> scenarioWithState) {
        IAdvancedActiveScenarioEntity existingActiveScenario = activeScenarioEntities.stream().filter(activeScenarioEntitie -> activeScenarioEntitie.getScenarioName().equals(scenarioName)).findFirst().orElse(null);
        if (existingActiveScenario != null) {
            if (scenarioWithState.getNextScenario() != null) {
                IAdvancedActiveScenarioEntity existingNextActiveScenario = activeScenarioEntities.stream().filter(activeScenarioEntitie -> activeScenarioEntitie.getScenarioName().equals(scenarioWithState.getNextScenario())).findFirst().orElse(null);
                if (existingNextActiveScenario != null) {
                    existingNextActiveScenario.setStatusName(findStateFromNewScenarioIfNeeded(scenarioWithState).toString());
                    return activeScenarioEntities.stream().filter(activeScenarioEntitie -> !activeScenarioEntitie.getScenarioName().equals(existingActiveScenario.getScenarioName())).collect(Collectors.toList());
                } else {
                    existingActiveScenario.setScenarioName(scenarioWithState.getNextScenario());
                    existingActiveScenario.setStatusName(findStateFromNewScenarioIfNeeded(scenarioWithState).toString());
                }
            } else {
                existingActiveScenario.setStatusName(findStateFromNewScenarioIfNeeded(scenarioWithState).toString());
            }

            return null;
        } else {
            return Stream.concat(
                    activeScenarioEntities.stream(),
                    Stream.of(
                            activeScenarioFactory.createActiveScenarioEntity(
                                    scenarioWithState.getNextScenario() != null ? scenarioWithState.getNextScenario() : scenarioName,
                                    findStateFromNewScenarioIfNeeded(scenarioWithState)
                            )
                    )
            ).collect(Collectors.toList());
        }
    }


    private Enum<?> findStateFromNewScenarioIfNeeded(ScenarioWithState<?> scenarioWithState) {
        if (scenarioWithState.getScenarioState() == null) {
            return advancedScenarioManager.findScenarioByName(scenarioWithState.getNextScenario()).getStartState();
        } else {
            return scenarioWithState.getScenarioState();
        }
    }


    private static UpdateRequestMapping fromTelegramRequest(@NotNull TelegramRequest request) {
        String pattern = request.getPatterns().stream().findFirst().orElse("**");
        RequestType requestType = request.getRequestTypes().stream().findFirst().orElse(null);
        MessageType messageType = request.getMessageTypes().stream().findFirst().orElse(null);

        // Create a Set with a single element if messageType is not null, otherwise an empty Set
        Set<MessageType> messageTypes = messageType != null ? Set.of(messageType) : Collections.emptySet();

        return new UpdateRequestMapping(pattern, requestType, messageTypes, request.exclusiveMessageType());
    }

}
