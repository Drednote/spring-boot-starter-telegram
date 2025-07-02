package io.github.drednote.telegram.handler.scenario.configurer;

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
}
