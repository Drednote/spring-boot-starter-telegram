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
     * @return the source {@link State} of the transition
     */
    State<S> getSource();

    /**
     * Retrieves the target state of the transition.
     *
     * @return the target {@link State} of the transition
     */
    State<S> getTarget();
}
