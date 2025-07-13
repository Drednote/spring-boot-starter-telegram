package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.config.model.verifier.StateMachineModelVerifier;

/**
 * Base {@code ScenarioMonitoringConfigurer} interface for configuring state machine verifier.
 *
 * @param <S> the type of state
 * @author Ivan Galushko
 */
public interface ScenarioVerifierConfigurer<S> extends ScenarioConfigConfigurerBuilder<S> {

    /**
     * Specify if verifier is enabled. On default verifier is enabled.
     *
     * @param enabled the enable flag
     * @return configurer for chaining
     */
    ScenarioVerifierConfigurer<S> enabled(boolean enabled);

    /**
     * Specify a custom model verifier.
     *
     * @param verifier the state machine model verifier
     * @return configurer for chaining
     */
    ScenarioVerifierConfigurer<S> verifier(StateMachineModelVerifier<S, ScenarioEvent> verifier);
}
