//package io.github.drednote.telegram.handler.scenario;
//
//
//import io.github.drednote.telegram.handler.scenario.data.ScenarioState;
//import io.github.drednote.telegram.handler.scenario.data.SimpleScenarioState;
//import io.github.drednote.telegram.handler.scenario.data.Transition;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//public class SimpleScenarioConfig<S> implements ScenarioConfig<S> {
//
//    private final ScenarioState<S> initial;
//    private final Map<S, List<Transition<S>>> states;
//    private final ScenarioIdResolver idResolver;
//    private final Set<S> terminateStates;
//
//    public SimpleScenarioConfig(
//        ScenarioState<S> initial, Map<S, List<Transition<S>>> states, Set<S> terminalStates,
//        ScenarioIdResolver idResolver
//    ) {
//        this.initial = initial;
//        this.states = states;
//        List<S> allStates = states.values().stream()
//            .flatMap(Collection::stream)
//            .map(Transition::getTarget)
//            .map(target -> ((ScenarioState<S>) new SimpleScenarioState<>(target.getId())))
//            .toList();
//        Set<S> terminalStatesFull = new HashSet<>(terminalStates);
//        for (ScenarioState<S> emptyState : allStates) {
//            if (getTransitions(emptyState).isEmpty()) {
//                terminalStatesFull.add(emptyState);
//            }
//        }
//        this.idResolver = idResolver;
//        this.terminateStates = terminalStatesFull;
//    }
//
//    public SimpleScenarioConfig(
//        ScenarioState<S> initial, Map<S, List<Transition<S>>> states,
//        ScenarioIdResolver idResolver
//    ) {
//        this(initial, states, Collections.emptySet(), idResolver);
//    }
//
//    @Override
//    public ScenarioState<S> getInitial() {
//        return initial;
//    }
//
//    @Override
//    public Set<S> getTerminateStates() {
//        return terminateStates;
//    }
//
//    @Override
//    public List<Transition<S>> getTransitions(ScenarioState<S> state) {
//        List<Transition<S>> transitions = states.get(state);
//        if (transitions == null) {
//            return Collections.emptyList();
//        }
//        return new ArrayList<>(transitions);
//    }
//
//    @Override
//    public ScenarioIdResolver getIdResolver() {
//        return idResolver;
//    }
//}