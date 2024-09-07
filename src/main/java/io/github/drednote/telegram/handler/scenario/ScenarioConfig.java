package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.handler.scenario.persist.StateContext;
import io.github.drednote.telegram.handler.scenario.persist.TransitionContext;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ScenarioConfig<S> {

    State<S> getInitial();

    Set<State<S>> getTerminateStates();

    Optional<State<S>> findState(StateContext<S> context);

    List<Transition<S>> getTransitions(State<S> state);

    Optional<Transition<S>> findTransition(TransitionContext<S> context);
}
