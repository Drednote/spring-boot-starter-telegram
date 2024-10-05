package io.github.drednote.telegram.handler.scenario.persist;

/**
 * Represents a context for a scenario.
 *
 * @param <S> the type of the state in the scenario
 * @author Ivan Galushko
 */
public interface ScenarioContext<S> {

    /**
     * Retrieves the unique identifier of the scenario.
     *
     * @return a {@code String} representing the scenario's unique identifier
     */
    String id();

    /**
     * Retrieves the state context associated with the scenario.
     *
     * @return a {@code StateContext<S>} representing the current state context of the scenario
     */
    StateContext<S> state();
}

