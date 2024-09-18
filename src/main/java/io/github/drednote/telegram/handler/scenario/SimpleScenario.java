package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.handler.scenario.data.SimpleState;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import io.github.drednote.telegram.handler.scenario.persist.StateContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public class SimpleScenario<S> implements Scenario<S>, ScenarioAccessor<S> {

    private static final Logger log = LoggerFactory.getLogger(SimpleScenario.class);
    private final ScenarioConfig<S> config;
    private final ScenarioPersister<S> persister;

    private String id;
    private State<S> state;

    public SimpleScenario(
        String id, ScenarioConfig<S> scenarioConfig, ScenarioPersister<S> persister
    ) {
        this.id = id;
        this.state = scenarioConfig.getInitial();
        this.config = scenarioConfig;
        this.persister = persister;
    }

    @Override
    public boolean sendEvent(UpdateRequest request) {
        if (isTerminated()) {
            return false;
        }
        synchronized (this) {
            Optional<Transition<S>> optionalSTransition = findTransition(request);
            if (optionalSTransition.isEmpty()) {
                return false;
            }
            Transition<S> transition = optionalSTransition.get();
            State<S> target = transition.getTarget();

            var context = new SimpleActionContext<>(request, transition);
            try {
                Object response = target.execute(context);
                ResponseSetter.setResponse(request, response);
            } catch (Exception e) {
                log.error("Unhandled exception", e);
                return false;
            }

            this.state = target;

            persister.persist(this);
        }
        return true;
    }

    @Override
    public boolean matches(UpdateRequest request) {
        return findTransition(request).isPresent();
    }

    @Override
    public boolean isTerminated() {
        return config.getTransitions(new SimpleState<>(state.getId())).isEmpty();
    }

    private Optional<Transition<S>> findTransition(UpdateRequest request) {
        List<Transition<S>> transitions = config.getTransitions(new SimpleState<>(state.getId()));
        for (Transition<S> transition : transitions) {
            if (transition.getTarget().matches(request)) {
                return Optional.of(transition);
            }
        }
        return Optional.empty();
    }

    @Override
    public void resetScenario(ScenarioContext<S> context) {
        if (!context.id().equals(id)) {
            throw new IllegalStateException("Cannot reset scenario because it does not match id");
        }

        synchronized (this) {
            state = convertToState(context.state());
        }
    }

    private @NonNull SimpleState<S> convertToState(StateContext<S> stateContext) {
        Set<? extends UpdateRequestMappingAccessor> mappings = stateContext.updateRequestMappings();
        SimpleState<S> simpleState = new SimpleState<>(stateContext.id(), convert(mappings));
        simpleState.setCallbackQueryState(stateContext.callbackQuery());
        simpleState.setOverrideGlobalScenarioId(stateContext.overrideGlobalScenarioId());
        return simpleState;
    }

    private Set<UpdateRequestMapping> convert(Set<? extends UpdateRequestMappingAccessor> mappings) {
        return mappings.stream().map(mapping -> {
            if (mapping instanceof UpdateRequestMapping updateRequestMapping) {
                return updateRequestMapping;
            }
            return new UpdateRequestMapping(mapping.getPattern(), mapping.getRequestType(), mapping.getMessageTypes());
        }).collect(Collectors.toSet());
    }

    @Override
    public ScenarioIdResolver getIdResolver() {
        return config.getIdResolver();
    }

    @Override
    public ScenarioPersister<S> getPersister() {
        return persister;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public State<S> getState() {
        return state;
    }

    @Override
    public ScenarioAccessor<S> getAccessor() {
        return this;
    }
}
