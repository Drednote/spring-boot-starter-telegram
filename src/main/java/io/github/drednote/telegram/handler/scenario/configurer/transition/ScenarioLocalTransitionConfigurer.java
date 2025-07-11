package io.github.drednote.telegram.handler.scenario.configurer.transition;

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
