package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

public class SimpleScenarioStateConfigurer<S> implements ScenarioStateConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final StateMachineStateConfigurer<S, ScenarioEvent> configurer;
    private final org.springframework.statemachine.config.configurers.StateConfigurer<S, ScenarioEvent> withStates;

    public SimpleScenarioStateConfigurer(ScenarioBuilder<S> builder) throws Exception {
        this.builder = builder;
        this.configurer = builder.configureStates();
        this.withStates = configurer.withStates();
    }

//    public SimpleScenarioStateConfigurer(
//        ScenarioBuilder<S> builder,
//        StateMachineStateConfigurer<S, ScenarioEvent> configurer
//    ) {
//        this.builder = builder;
//        this.configurer = configurer;
//    }

    @Override
    public StateConfigurer<S> withStates() throws Exception {
        return new DefaultStateConfigurer<>(builder, withStates);
    }
}
