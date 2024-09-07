package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
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

    public record TransitionData<S>(
            S source, S target, List<Action> actions, TelegramRequest request
    ) {}
}
