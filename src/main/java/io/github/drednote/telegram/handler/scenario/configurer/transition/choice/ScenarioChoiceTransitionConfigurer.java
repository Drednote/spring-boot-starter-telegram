package io.github.drednote.telegram.handler.scenario.configurer.transition.choice;

import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurerBuilder;

/**
 * Interface for configuring a choice transition.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioChoiceTransitionConfigurer<S>
    extends ScenarioTransitionConfigurerBuilder<S>,
    BaseChoiceScenarioTransitionConfigurer<S, ScenarioChoiceTransitionConfigurer<S>> {
}
