package io.github.drednote.telegram.handler.scenario.configurer.state;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.util.Set;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

public class DefaultScenarioStateConfigurer<S> implements ScenarioStateConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final StateMachineStateConfigurer<S, ScenarioEvent> configurer;
    private final Set<S> states;

    public DefaultScenarioStateConfigurer(
        ScenarioBuilder<S> builder, Set<S> states
    ) {
        this.builder = builder;
        this.configurer = builder.configureStates();
        this.states = states;
    }

    public DefaultScenarioStateConfigurer(
        ScenarioBuilder<S> builder,
        StateMachineStateConfigurer<S, ScenarioEvent> configurer, Set<S> states
    ) {
        this.builder = builder;
        this.configurer = configurer;
        this.states = states;
    }

    @Override
    public StateConfigurer<S> withStates() throws Exception {
        return new DefaultStateConfigurer<>(builder, configurer.withStates(), states);
    }
}
