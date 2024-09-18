package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer.TransitionData;

public class SimpleScenarioInlineMessageTransitionConfigurer<S>
    extends SimpleScenarioBaseTransitionConfigurer<ScenarioInlineMessageTransitionConfigurer<S>, S>
    implements ScenarioInlineMessageTransitionConfigurer<S> {

    public SimpleScenarioInlineMessageTransitionConfigurer(ScenarioBuilder<S> builder) {
        super(builder);
    }

    @Override
    protected void beforeAnd(TransitionData<S> data) {
        data.setCallBackQuery(true);
    }
}
