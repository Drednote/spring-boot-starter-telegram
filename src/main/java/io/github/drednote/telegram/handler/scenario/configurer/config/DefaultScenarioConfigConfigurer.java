package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;

public class DefaultScenarioConfigConfigurer<S> implements ScenarioConfigConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final StateMachineConfigurationConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioConfigConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
        this.configurer = builder.configureConfiguration();
    }

    public DefaultScenarioConfigConfigurer(
        ScenarioBuilder<S> builder,
        StateMachineConfigurationConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioConfigConfigurer<S> withPersister(ScenarioPersister<S> persister) {
        builder.setPersister(persister);
        return this;
    }

    @Override
    public ScenarioConfigConfigurer<S> withIdResolver(ScenarioIdResolver resolver) {
        builder.setResolver(resolver);
        return this;
    }

    @Override
    public ScenarioConfigurationConfigurer<S> withConfiguration() throws Exception {
        return new DefaultScenarioConfigurationConfigurer<>(builder, configurer.withConfiguration());
    }

    @Override
    public ScenarioDistributedConfigurer<S> withDistributed() throws Exception {
        return new DefaultScenarioDistributedConfigurer<>(builder, configurer.withDistributed());
    }

    @Override
    public ScenarioVerifierConfigurer<S> withVerifier() throws Exception {
        return new DefaultScenarioVerifierConfigurer<>(builder, configurer.withVerifier());
    }

    @Override
    public ScenarioMonitoringConfigurer<S> withMonitoring() throws Exception {
        return new DefaultScenarioMonitoringConfigurer<>(builder, configurer.withMonitoring());
    }
}
