package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.spy.ScenarioStateMachineMonitor;
import org.springframework.statemachine.config.configurers.MonitoringConfigurer;
import org.springframework.statemachine.monitor.StateMachineMonitor;

public class DefaultScenarioMonitoringConfigurer<S> implements ScenarioMonitoringConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final MonitoringConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioMonitoringConfigurer(
        ScenarioBuilder<S> builder, MonitoringConfigurer<S, ScenarioEvent> configurer
    ) {
        this.configurer = configurer;
        this.builder = builder;
    }

    @Override
    public ScenarioMonitoringConfigurer<S> monitor(ScenarioStateMachineMonitor<S> monitor) {
        builder.addMonitor(monitor);
        return this;
    }

    @Override
    public ScenarioMonitoringConfigurer<S> monitor(StateMachineMonitor<S, ScenarioEvent> monitor) {
        configurer.monitor(monitor);
        return this;
    }

    @Override
    public ScenarioConfigConfigurer<S> and() throws Exception {
        return new DefaultScenarioConfigConfigurer<>(builder, configurer.and());
    }
}
