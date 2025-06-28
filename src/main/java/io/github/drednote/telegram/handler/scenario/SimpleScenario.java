//package io.github.drednote.telegram.handler.scenario;
//
//import io.github.drednote.telegram.core.ResponseSetter;
//import io.github.drednote.telegram.core.request.UpdateRequest;
//import io.github.drednote.telegram.core.request.UpdateRequestMapping;
//import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
//import io.github.drednote.telegram.exception.type.ScenarioException;
//import io.github.drednote.telegram.handler.scenario.ScenarioEventResult.SimpleScenarioEventResult;
//import io.github.drednote.telegram.handler.scenario.data.ScenarioState;
//import io.github.drednote.telegram.handler.scenario.data.SimpleScenarioState;
//import io.github.drednote.telegram.handler.scenario.data.Transition;
//import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
//import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
//import io.github.drednote.telegram.handler.scenario.persist.StateContext;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.lang.NonNull;
//
//public class SimpleScenario<S> implements Scenario<S>, ScenarioAccessor<S> {
//
//    private static final Logger log = LoggerFactory.getLogger(SimpleScenario.class);
//    private final ScenarioConfig<S> config;
//    private final ScenarioPersister<S> persister;
//
//    private String id;
//    private ScenarioState<S> state;
//
//    public SimpleScenario(
//        String id, ScenarioConfig<S> scenarioConfig, ScenarioPersister<S> persister
//    ) {
//        this.id = id;
//        this.state = scenarioConfig.getInitial();
//        this.config = scenarioConfig;
//        this.persister = persister;
//    }
//
//    @Override
//    public ScenarioEventResult sendEvent(UpdateRequest request) {
//        synchronized (this) {
//            if (isTerminated()) {
//                return new SimpleScenarioEventResult(false,
//                    new ScenarioException("Scenario has been terminated"));
//            }
//            Optional<Transition<S>> optionalSTransition = findTransition(request);
//            if (optionalSTransition.isEmpty()) {
//                return new SimpleScenarioEventResult(false,
//                    new ScenarioException("Transition not found"));
//            }
//            Transition<S> transition = optionalSTransition.get();
//            ScenarioState<S> target = transition.getTarget();
//            Map<String, Object> props = target.getProps();
//
//            var context = new SimpleActionContext<>(request, transition, new HashMap<>(props));
//            try {
//                Object response = target.execute(context);
//                ResponseSetter.setResponse(request, response);
//            } catch (Exception e) {
//                return new SimpleScenarioEventResult(false,
//                    new ScenarioException("During scenario event unhandled exception happened", e));
//            }
//
//            this.state = target;
//            return new SimpleScenarioEventResult(true, null);
//        }
//    }
//
//    @Override
//    public boolean matches(UpdateRequest request) {
//        return findTransition(request).isPresent();
//    }
//
//    @Override
//    public boolean isTerminated() {
//        SimpleScenarioState<S> emptyState = new SimpleScenarioState<>(state.getId());
//        return config.getTerminateStates().contains(emptyState);
//    }
//
//    private Optional<Transition<S>> findTransition(UpdateRequest request) {
//        List<Transition<S>> transitions = config.getTransitions(new SimpleScenarioState<>(state.getId()));
//        for (Transition<S> transition : transitions) {
//            if (transition.getTarget().matches(request)) {
//                return Optional.of(transition);
//            }
//        }
//        return Optional.empty();
//    }
//
//    @Override
//    public void resetScenario(ScenarioContext<S> context) {
//        if (!context.getId().equals(id)) {
//            throw new IllegalStateException("Cannot reset scenario because it does not match getId");
//        }
//
//        synchronized (this) {
//            state = convertToState(context.getMachine());
//        }
//    }
//
//    private @NonNull SimpleScenarioState<S> convertToState(StateContext<S> stateContext) {
//        Set<? extends UpdateRequestMappingAccessor> mappings = stateContext.updateRequestMappings();
//        SimpleScenarioState<S> simpleState = new SimpleScenarioState<>(stateContext.id(), convert(mappings), stateContext.props());
//        simpleState.setResponseMessageProcessing(stateContext.responseMessageProcessing());
//        return simpleState;
//    }
//
//    private Set<UpdateRequestMapping> convert(
//        Set<? extends UpdateRequestMappingAccessor> mappings) {
//        return mappings.stream().map(mapping -> {
//            if (mapping instanceof UpdateRequestMapping updateRequestMapping) {
//                return updateRequestMapping;
//            }
//            return new UpdateRequestMapping(mapping.getPattern(), mapping.getRequestType(),
//                mapping.getMessageTypes());
//        }).collect(Collectors.toSet());
//    }
//
//    @Override
//    public ScenarioIdResolver getIdResolver() {
//        return config.getIdResolver();
//    }
//
//    @Override
//    public ScenarioPersister<S> getPersister() {
//        return persister;
//    }
//
//    @Override
//    public String getId() {
//        return id;
//    }
//
//    @Override
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    @Override
//    public ScenarioState<S> getState() {
//        return state;
//    }
//
//    @Override
//    public ScenarioAccessor<S> getAccessor() {
//        return this;
//    }
//}
