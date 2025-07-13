package io.github.drednote.telegram.handler.scenario.configurer.transition;

/**
 * Interface for configuring an internal transition.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioInternalTransitionConfigurer<S> extends
    ScenarioBaseTransitionConfigurer<ScenarioInternalTransitionConfigurer<S>, S> {
}
