package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

public class DefaultScenarioTransitionConfigurer<S> implements ScenarioTransitionConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final StateMachineTransitionConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioTransitionConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
        this.configurer = builder.configureTransitions();
    }

    public DefaultScenarioTransitionConfigurer(
        ScenarioBuilder<S> builder,
        StateMachineTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioExternalTransitionConfigurer<S> withExternal() throws Exception {
        return new DefaultScenarioExternalTransitionConfigurer<>(builder, configurer.withExternal());
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> withRollback() throws Exception {
        return new DefaultScenarioRollbackTransitionConfigurer<>(builder, configurer.withExternal());
    }
}
