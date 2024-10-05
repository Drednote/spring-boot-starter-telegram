package io.github.drednote.telegram.handler.scenario.configurer;

import org.springframework.lang.Nullable;

/**
 * Interface for configuring scenario states.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioStateConfigurer<S> {

    /**
     * Sets the initial state for the scenario.
     *
     * @param state the initial state to set
     * @return the current instance of ScenarioStateConfigurer
     */
    ScenarioStateConfigurer<S> withInitialState(S state);

    /**
     * Sets the terminal states for the scenario.
     *
     * @param state the terminal state to set
     * @param states others terminal states to set
     * @return the current instance of ScenarioStateConfigurer
     */
    ScenarioStateConfigurer<S> withTerminalState(S state, @Nullable S... states);
}
