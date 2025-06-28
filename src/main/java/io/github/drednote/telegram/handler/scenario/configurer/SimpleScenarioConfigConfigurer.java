package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;

public class SimpleScenarioConfigConfigurer<S> implements ScenarioConfigConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final StateMachineConfigurationConfigurer<S, ScenarioEvent> configurer;

    public SimpleScenarioConfigConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
        this.configurer = builder.configureConfiguration();
    }

    public SimpleScenarioConfigConfigurer(
        ScenarioBuilder<S> builder,
        StateMachineConfigurationConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioConfigConfigurer<S> withAdapter(ScenarioRepositoryAdapter<S> adapter) {
        builder.setAdapter(adapter);
        return this;
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
