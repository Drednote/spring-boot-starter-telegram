package io.github.drednote.telegram.handler.scenario.configurer.transition;

/**
 * Interface for configuring scenario transitions.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioTransitionConfigurer<S> {

    /**
     * Configures an external transition.
     * <p>
     * This is the default behavior for transition, which is just creating transition with specified
     * parameters.
     *
     * @return a {@link ScenarioExternalTransitionConfigurer} instance for further configuration
     */
    ScenarioExternalTransitionConfigurer<S> withExternal() throws Exception;

    /**
     * Configures a rollback transition.
     * <p>
     * During this configurer will be created a new one transition with a reverse direction. For
     * example, you create A → B transition, there will be created B → A transition with personal
     * telegramRequest matching and action (you should specify it).
     *
     * @return a {@link ScenarioRollbackTransitionConfigurer} instance for further configuration
     */
    ScenarioRollbackTransitionConfigurer<S> withRollback() throws Exception;
}
