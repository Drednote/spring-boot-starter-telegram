package io.github.drednote.telegram.handler.scenario.configurer.transition;

/**
 * Interface for configuring an external transition.
 * <p>
 * This is the default behavior for transition, which is just creating transition with specified parameters.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioExternalTransitionConfigurer<S> extends
    ScenarioBaseTransitionConfigurer<ScenarioExternalTransitionConfigurer<S>, S> {

    /**
     * Sets the target state for the transition.
     *
     * @param target the target getMachine for the transition
     * @return the current instance of the configurer
     */
    ScenarioExternalTransitionConfigurer<S> target(S target);

    /**
     * After execution of this transition and processing response message to user, id of the current scenario will be
     * changed to messageId of a response message from a telegram.
     * <p>
     * Use this method if you during transition creating a message with inline keyboard, and want to interact with this
     * message independently of other scenarios.
     */
    ScenarioExternalTransitionConfigurer<S> inlineKeyboardCreation();
}
