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
    ScenarioExternalTransitionConfigurer<S> withExternal();

    /**
     * Configures a transition with response message processing.
     * <p>
     * After execution of this transition and processing response message to user, id of the current
     * scenario will be changed to messageId of a response message from a telegram.
     * <p>
     * Use this type if you during transition creating a message with inline keyboard, and want to
     * interact with this message independently of other scenarios.
     *
     * @return a {@link ScenarioResponseMessageTransitionConfigurer} instance for further
     * configuration
     */
    ScenarioResponseMessageTransitionConfigurer<S> withResponseMessageProcessing();

    /**
     * Configures a rollback transition.
     * <p>
     * During this configurer will be created a new one transition with a reverse direction. For
     * example, you create A → B transition, there will be created B → A transition with personal
     * telegramRequest matching and action (you should specify it).
     *
     * @return a {@link ScenarioRollbackTransitionConfigurer} instance for further configuration
     */
    ScenarioRollbackTransitionConfigurer<S> withRollback();
}
