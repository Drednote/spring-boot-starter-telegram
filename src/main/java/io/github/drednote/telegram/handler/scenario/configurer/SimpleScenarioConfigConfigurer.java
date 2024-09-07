package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.ScenarioIdResolver;

public class SimpleScenarioConfigConfigurer<S> implements ScenarioConfigConfigurer<S> {

    private final ScenarioBuilder<S> scenarioBuilder;

    public SimpleScenarioConfigConfigurer(ScenarioBuilder<S> scenarioBuilder) {
        this.scenarioBuilder = scenarioBuilder;
    }

    @Override
    public ScenarioConfigConfigurer<S> withPersister(ScenarioRepositoryAdapter<S> adapter) {
        scenarioBuilder.setAdapter(adapter);
        return this;
    }

    @Override
    public ScenarioConfigConfigurer<S> withIdResolver(ScenarioIdResolver resolver) {
        scenarioBuilder.setResolver(resolver);
        return this;
    }
}
