package io.github.drednote.telegram.handler.scenario.configurer.transition;

/**
 * Interface for configuring an external transition.
 * <p>
 * This is the default behavior for transition, which is just creating transition with specified
 * parameters.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioExternalTransitionConfigurer<S> extends
    ScenarioBaseTransitionConfigurer<ScenarioExternalTransitionConfigurer<S>, S> {

}
