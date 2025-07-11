package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.ensemble.StateMachineEnsemble;

public interface ScenarioDistributedConfigurer<S> extends ScenarioConfigConfigurerBuilder<S> {

    /**
     * Specify a {@link StateMachineEnsemble}.
     *
     * @param ensemble the state machine ensemble
     * @return configurer for chaining
     */
    ScenarioDistributedConfigurer<S> ensemble(StateMachineEnsemble<S, ScenarioEvent> ensemble);
}
