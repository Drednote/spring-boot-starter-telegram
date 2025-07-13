package io.github.drednote.telegram.handler.scenario.configurer.transition;

/**
 * Interface for configuring a local transition.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioLocalTransitionConfigurer<S> extends
    ScenarioBaseTransitionConfigurer<ScenarioLocalTransitionConfigurer<S>, S> {

    /**
     * Sets the target state for the transition.
     *
     * @param target the target getMachine for the transition
     * @return the current instance of the configurer
     */
    ScenarioLocalTransitionConfigurer<S> target(S target);
}
