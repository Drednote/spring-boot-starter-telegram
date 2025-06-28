package io.github.drednote.telegram.handler.scenario.data;

/**
 * Represents a Transition between states in the scenario.
 *
 * @param <S> the type of the state identifier
 * @author Ivan Galushko
 */
public interface Transition<S> {

    /**
     * Retrieves the source state of the transition.
     *
     * @return the source {@link ScenarioState} of the transition
     */
    ScenarioState<S> getSource();

    /**
     * Retrieves the target state of the transition.
     *
     * @return the target {@link ScenarioState} of the transition
     */
    ScenarioState<S> getTarget();
}
