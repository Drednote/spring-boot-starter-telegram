package io.github.drednote.telegram.handler.scenario.configurer.transition.choice;

import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurerBuilder;

public interface ScenarioJunctionTransitionConfigurer<S>
    extends ScenarioTransitionConfigurerBuilder<S>,
    BaseScenarioTransitionConfigurer<S, ScenarioJunctionTransitionConfigurer<S>> {
}
