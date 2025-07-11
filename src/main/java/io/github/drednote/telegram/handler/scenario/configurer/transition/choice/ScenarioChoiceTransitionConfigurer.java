package io.github.drednote.telegram.handler.scenario.configurer.transition.choice;

import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurerBuilder;

public interface ScenarioChoiceTransitionConfigurer<S>
    extends ScenarioTransitionConfigurerBuilder<S>,
    BaseScenarioTransitionConfigurer<S, ScenarioChoiceTransitionConfigurer<S>> {
}
