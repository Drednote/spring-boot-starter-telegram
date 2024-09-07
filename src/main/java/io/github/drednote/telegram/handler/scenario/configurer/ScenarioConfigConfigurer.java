package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.ScenarioIdResolver;

public interface ScenarioConfigConfigurer<S> {

    ScenarioConfigConfigurer<S> withPersister(ScenarioRepositoryAdapter<S> adapter);

    ScenarioConfigConfigurer<S> withIdResolver(ScenarioIdResolver resolver);
}
