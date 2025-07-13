package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.spy.ScenarioStateMachineMonitor;
import org.springframework.statemachine.monitor.StateMachineMonitor;

public interface ScenarioMonitoringConfigurer<S> extends ScenarioConfigConfigurerBuilder<S> {

    /**
     * Specify a scenario monitor. This monitor calls only when transition is occurred and transition is not initial
     * (when statemachine is starting).
     *
     * @param monitor the state machine monitor
     * @return configurer for chaining
     */
    ScenarioMonitoringConfigurer<S> monitor(ScenarioStateMachineMonitor<S> monitor);

    /**
     * Specify a state machine monitor.
     *
     * @param monitor the state machine monitor
     * @return configurer for chaining
     */
    ScenarioMonitoringConfigurer<S> monitor(StateMachineMonitor<S, ScenarioEvent> monitor);
}
