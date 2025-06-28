package io.github.drednote.telegram.handler.scenario.configurer.transition;

/**
 * Interface for configuring a transition with response message processing.
 * <p>
 * After execution of this transition and processing response message to user, id of the current
 * scenario will be changed to messageId of a response message from a telegram.
 * <p>
 * Use this type if you during transition creating a message with inline keyboard, and want to interact with this
 * message independently of other scenarios.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioResponseMessageTransitionConfigurer<S> extends
    ScenarioBaseTransitionConfigurer<ScenarioResponseMessageTransitionConfigurer<S>, S> {

    /**
     * Sets the target state for the transition.
     *
     * @param target the target getMachine for the transition
     * @return the current instance of the configurer
     */
    ScenarioResponseMessageTransitionConfigurer<S> target(S target);
}
