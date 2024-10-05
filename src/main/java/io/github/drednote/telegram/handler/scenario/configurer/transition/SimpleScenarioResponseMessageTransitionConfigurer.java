package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer.TransitionData;

public class SimpleScenarioResponseMessageTransitionConfigurer<S>
    extends SimpleScenarioBaseTransitionConfigurer<ScenarioResponseMessageTransitionConfigurer<S>, S>
    implements ScenarioResponseMessageTransitionConfigurer<S> {

    public SimpleScenarioResponseMessageTransitionConfigurer(ScenarioBuilder<S> builder) {
        super(builder);
    }

    @Override
    protected void beforeAnd(TransitionData<S> data) {
        data.setResponseMessageProcessing(true);
    }
}
