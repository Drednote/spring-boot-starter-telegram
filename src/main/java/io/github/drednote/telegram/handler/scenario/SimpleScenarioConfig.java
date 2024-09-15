package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.handler.scenario.data.SimpleState;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import io.github.drednote.telegram.handler.scenario.persist.StateContext;
import io.github.drednote.telegram.handler.scenario.persist.TransitionContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleScenarioConfig<S> implements ScenarioConfig<S> {

    private final State<S> initial;
    private final Map<State<S>, List<Transition<S>>> states;
    private final List<State<S>> allStates;
    private final ScenarioIdResolver idResolver;
    private final Set<State<S>> terminateStates;

    public SimpleScenarioConfig(
        State<S> initial, Map<State<S>, List<Transition<S>>> states, Set<State<S>> terminalStates,
        ScenarioIdResolver idResolver
    ) {
        this.initial = initial;
        this.states = states;
        this.allStates = states.values().stream()
            .flatMap(Collection::stream)
            .map(Transition::getTarget)
            .toList();
        this.idResolver = idResolver;
        Set<State<S>> terminateStates = this.allStates.stream()
            .filter(state -> !states.containsKey(state))
            .collect(Collectors.toSet());
        terminateStates.addAll(terminalStates);
        this.terminateStates = Collections.unmodifiableSet(terminateStates);
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
    public Optional<State<S>> findState(StateContext<S> context) {
        return allStates.stream()
            .filter(state ->
                state.getId().equals(context.id()) &&
                state.getUpdateRequestMappings().equals(context.updateRequestMappings())
            ).findFirst();
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
    public Optional<Transition<S>> findTransition(TransitionContext<S> context) {
        S sourceId = context.getSourceContext().id();
        S targetId = context.getTargetContext().id();
        return states.get(new SimpleState<>(sourceId))
            .stream()
            .filter(transition -> transition.getTarget().equals(new SimpleState<>(targetId)))
            .findFirst();
    }

    @Override
    public ScenarioIdResolver getIdResolver() {
        return idResolver;
    }
}