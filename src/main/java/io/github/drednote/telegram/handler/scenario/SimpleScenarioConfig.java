package io.github.drednote.telegram.handler.scenario;


import io.github.drednote.telegram.handler.scenario.data.SimpleState;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleScenarioConfig<S> implements ScenarioConfig<S> {

    private final State<S> initial;
    private final Map<State<S>, List<Transition<S>>> states;
    private final ScenarioIdResolver idResolver;
    private final Set<State<S>> terminateStates;

    public SimpleScenarioConfig(
        State<S> initial, Map<State<S>, List<Transition<S>>> states, Set<State<S>> terminalStates,
        ScenarioIdResolver idResolver
    ) {
        this.initial = initial;
        this.states = states;
        List<State<S>> allStates = states.values().stream()
            .flatMap(Collection::stream)
            .map(Transition::getTarget)
            .map(target -> ((State<S>) new SimpleState<>(target.getId())))
            .toList();
        Set<State<S>> terminalStatesFull = new HashSet<>(terminalStates);
        for (State<S> emptyState : allStates) {
            if (getTransitions(emptyState).isEmpty()) {
                terminalStatesFull.add(emptyState);
            }
        }
        this.idResolver = idResolver;
        this.terminateStates = terminalStatesFull;
    }

    public SimpleScenarioConfig(
        State<S> initial, Map<State<S>, List<Transition<S>>> states,
        ScenarioIdResolver idResolver
    ) {
        this(initial, states, Collections.emptySet(), idResolver);
    }

    @Override
    public State<S> getInitial() {
        return initial;
    }

    @Override
    public Set<State<S>> getTerminateStates() {
        return terminateStates;
    }

    @Override
    public List<Transition<S>> getTransitions(State<S> state) {
        List<Transition<S>> transitions = states.get(state);
        if (transitions == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(transitions);
    }

    @Override
    public ScenarioIdResolver getIdResolver() {
        return idResolver;
    }
}