package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.config.configurers.VerifierConfigurer;
import org.springframework.statemachine.config.model.verifier.StateMachineModelVerifier;

public class DefaultScenarioVerifierConfigurer<S> implements ScenarioVerifierConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final VerifierConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioVerifierConfigurer(
        ScenarioBuilder<S> builder, VerifierConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioVerifierConfigurer<S> enabled(boolean enabled) {
        configurer.enabled(enabled);
        return this;
    }

    @Override
    public ScenarioVerifierConfigurer<S> verifier(StateMachineModelVerifier<S, ScenarioEvent> verifier) {
        configurer.verifier(verifier);
        return this;
    }

    @Override
    public ScenarioConfigConfigurer<S> and() throws Exception {
        return new DefaultScenarioConfigConfigurer<>(builder, configurer.and());
    }
}
