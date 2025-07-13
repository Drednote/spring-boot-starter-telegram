package io.github.drednote.telegram.handler.scenario.configurer.transition.choice;

import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurerBuilder;

/**
 * Interface for configuring a junction transition.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioJunctionTransitionConfigurer<S>
    extends ScenarioTransitionConfigurerBuilder<S>,
    BaseChoiceScenarioTransitionConfigurer<S, ScenarioJunctionTransitionConfigurer<S>> {
}
