package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

public class SimpleScenarioTransitionConfigurer<S> implements ScenarioTransitionConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final StateMachineTransitionConfigurer<S, ScenarioEvent> configurer;

    public SimpleScenarioTransitionConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
        this.configurer = builder.configureTransitions();
    }

    public SimpleScenarioTransitionConfigurer(
        ScenarioBuilder<S> builder,
        StateMachineTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioExternalTransitionConfigurer<S> withExternal() throws Exception {
        return new SimpleScenarioExternalTransitionConfigurer<>(builder, configurer.withExternal());
    }

    @Override
    public ScenarioResponseMessageTransitionConfigurer<S> withResponseMessageProcessing() throws Exception {
        return new SimpleScenarioResponseMessageTransitionConfigurer<>(builder, configurer.withExternal());
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> withRollback() throws Exception {
        return new SimpleScenarioRollbackTransitionConfigurer<>(builder, configurer.withExternal());
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
