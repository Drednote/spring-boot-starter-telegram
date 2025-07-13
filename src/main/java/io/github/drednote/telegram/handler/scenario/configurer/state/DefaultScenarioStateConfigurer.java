package io.github.drednote.telegram.handler.scenario.configurer.state;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

public class DefaultScenarioStateConfigurer<S> implements ScenarioStateConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final StateMachineStateConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioStateConfigurer(
        ScenarioBuilder<S> builder
    ) {
        this.builder = builder;
        this.configurer = builder.configureStates();
    }

    public DefaultScenarioStateConfigurer(
        ScenarioBuilder<S> builder,
        StateMachineStateConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public StateConfigurer<S> withStates() throws Exception {
        return new DefaultStateConfigurer<>(builder, configurer.withStates());
    }
}
