package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.handler.scenario.spy.ScenarioStateMachineMonitor;

public interface ScenarioMonitoringConfigurer<S> extends ScenarioConfigConfigurerBuilder<S> {

    /**
     * Specify a state machine monitor.
     *
     * @param monitor the state machine monitor
     * @return configurer for chaining
     */
    ScenarioMonitoringConfigurer<S> monitor(ScenarioStateMachineMonitor<S> monitor);
}
