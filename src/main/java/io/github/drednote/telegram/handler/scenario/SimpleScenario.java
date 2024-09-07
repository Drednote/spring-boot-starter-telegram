package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleScenario<S> implements Scenario<S>, ScenarioAccessor<S> {

    private static final Logger log = LoggerFactory.getLogger(SimpleScenario.class);
    private final String id;
    private final ScenarioConfig<S> config;
    private final ScenarioPersister<S> persister;
    private final List<Transition<S>> history;

    private State<S> state;

    public SimpleScenario(
            String id, ScenarioConfig<S> scenarioConfig, ScenarioPersister<S> persister
    ) {
        this.id = id;
        this.state = scenarioConfig.getInitial();
        this.config = scenarioConfig;
        this.persister = persister;
        this.history = new ArrayList<>();
    }

    @Override
    public boolean sendEvent(UpdateRequest request) {
        if (isTerminated()) {
            return false;
        }
        synchronized (this) {
            findTransition(request).ifPresent(transition -> {
                State<S> target = transition.getTarget();

                var context = new SimpleActionContext(request);
                Object response = target.execute(context);

                request.setResponse(response);
                this.state = target;
                history.add(transition);

                persister.persist(this);
            });
        }
        return true;
    }

    @Override
    public boolean matches(UpdateRequest request) {
        return findTransition(request).isPresent();
    }

    @Override
    public boolean isTerminated() {
        return config.getTerminateStates().contains(state);
    }

    @Override
    public ScenarioAccessor<S> getAccessor() {
        return this;
    }

    @Override
    public List<? extends Transition<S>> getTransitionsHistory() {
        return new ArrayList<>(history);
    }

    private Optional<Transition<S>> findTransition(UpdateRequest request) {
        List<Transition<S>> transitions = config.getTransitions(state);
        for (Transition<S> transition : transitions) {
            if (transition.getTarget().matches(request)) {
                return Optional.of(transition);
            }
        }
        return Optional.empty();
    }

    @Override
    public void resetScenario(ScenarioContext<S> context) {
        if (!context.getId().equals(id)) {
            throw new IllegalStateException("Cannot reset scenario because it does not match id");
        }

        synchronized (this) {
            state = config.findState(context.getState())
                    .orElseThrow(() -> new IllegalStateException("No state found"));
            history.clear();
            history.addAll(context.getTransitionsHistory().stream()
                    .map(history -> config.findTransition(history)
                            .orElseThrow(() -> new IllegalArgumentException("transition not found")))
                    .toList());
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public State<S> getState() {
        return state;
    }
}
