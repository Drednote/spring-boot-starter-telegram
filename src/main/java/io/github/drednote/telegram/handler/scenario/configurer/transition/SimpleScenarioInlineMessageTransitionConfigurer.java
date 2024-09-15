package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;

public class SimpleScenarioInlineMessageTransitionConfigurer<S>
    extends SimpleScenarioBaseTransitionConfigurer<ScenarioInlineMessageTransitionConfigurer<S>, S>
    implements ScenarioInlineMessageTransitionConfigurer<S> {

    public SimpleScenarioInlineMessageTransitionConfigurer(ScenarioBuilder<S> builder) {
        super(builder);
        this.callBackQuery = true;
    }
}
