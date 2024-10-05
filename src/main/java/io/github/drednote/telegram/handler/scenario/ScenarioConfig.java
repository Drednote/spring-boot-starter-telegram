package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import java.util.List;
import java.util.Set;

/**
 * Interface that stores all configuration for a scenario.
 *
 * @param <S> the type of the state managed by the scenario
 * @author Ivan Galushko
 */
public interface ScenarioConfig<S> {

    /**
     * Retrieves the initial state of the scenario.
     *
     * @return the initial state as a {@code State<S>} object
     */
    State<S> getInitial();

    /**
     * Retrieves the set of termination states for the scenario.
     *
     * @return a {@code Set<State<S>>} containing the termination states
     */
    Set<State<S>> getTerminateStates();

    /**
     * Retrieves the list of transitions available from the specified state.
     *
     * @param state the state for which to retrieve transitions
     * @return a {@code List<Transition<S>>} containing the transitions from the given state
     */
    List<Transition<S>> getTransitions(State<S> state);

    /**
     * Retrieves the scenario ID resolver.
     *
     * @return an instance of ScenarioIdResolver used for resolving scenario IDs
     */
    ScenarioIdResolver getIdResolver();
}

