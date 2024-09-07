package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.ScenarioConfig;
import io.github.drednote.telegram.handler.scenario.SimpleScenario;

public class SimpleScenarioFactory<S> implements ScenarioFactory<S> {

    private final ScenarioPersister<S> scenarioPersister;
    private final ScenarioConfig<S> config;

    public SimpleScenarioFactory(
            ScenarioConfig<S> config, ScenarioPersister<S> scenarioPersister
    ) {
        this.scenarioPersister = scenarioPersister;
        this.config = config;
    }

    @Override
    public Scenario<S> create(String scenarioId) {
        return new SimpleScenario<>(scenarioId, config, scenarioPersister);
    }
}
