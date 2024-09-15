package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;

public class SimpleScenarioExternalTransitionConfigurer<S>
    extends SimpleScenarioBaseTransitionConfigurer<ScenarioExternalTransitionConfigurer<S>, S>
    implements ScenarioExternalTransitionConfigurer<S> {


    public SimpleScenarioExternalTransitionConfigurer(ScenarioBuilder<S> builder) {
        super(builder);
    }
}
