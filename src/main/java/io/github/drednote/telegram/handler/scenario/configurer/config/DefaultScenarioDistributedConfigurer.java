package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.config.configurers.DistributedStateMachineConfigurer;
import org.springframework.statemachine.ensemble.StateMachineEnsemble;

public class DefaultScenarioDistributedConfigurer<S> implements ScenarioDistributedConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final DistributedStateMachineConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioDistributedConfigurer(
        ScenarioBuilder<S> builder, DistributedStateMachineConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioDistributedConfigurer<S> ensemble(StateMachineEnsemble<S, ScenarioEvent> ensemble) {
        configurer.ensemble(ensemble);
        return this;
    }

    @Override
    public ScenarioConfigConfigurer<S> and() throws Exception {
        return new DefaultScenarioConfigConfigurer<>(builder, configurer.and());
    }
}
