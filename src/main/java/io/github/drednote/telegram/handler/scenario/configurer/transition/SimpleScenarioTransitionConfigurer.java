package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import java.util.List;

public class SimpleScenarioTransitionConfigurer<S> implements ScenarioTransitionConfigurer<S> {

    private final ScenarioBuilder<S> builder;

    public SimpleScenarioTransitionConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
    }

    @Override
    public ScenarioExternalTransitionConfigurer<S> withExternal() {
        return new SimpleScenarioExternalTransitionConfigurer<>(builder);
    }

    @Override
    public ScenarioInlineMessageTransitionConfigurer<S> withCreateInlineMessage() {
        return new SimpleScenarioInlineMessageTransitionConfigurer<>(builder);
    }

    public record TransitionData<S>(
            S source, S target, List<Action> actions, TelegramRequest request, boolean callBackQuery
    ) {}
}
