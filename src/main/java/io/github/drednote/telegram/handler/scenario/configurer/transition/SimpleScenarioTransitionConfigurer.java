package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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
    public ScenarioResponseMessageTransitionConfigurer<S> withResponseMessageProcessing() {
        return new SimpleScenarioResponseMessageTransitionConfigurer<>(builder);
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> withRollback() {
        return new SimpleScenarioRollbackTransitionConfigurer<>(builder);
    }

    @Data
    @RequiredArgsConstructor
    public static class TransitionData<S> {

        private final S source;
        private final S target;
        private final List<Action<S>> actions;
        private final TelegramRequest request;
        private final Map<String, Object> props;
        private boolean responseMessageProcessing = false;
    }
}
