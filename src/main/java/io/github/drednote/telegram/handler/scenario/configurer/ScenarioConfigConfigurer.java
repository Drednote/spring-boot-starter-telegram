package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.SimpleScenarioIdResolver;

/**
 * Interface for configuring general scenario settings.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioConfigConfigurer<S> {

    /**
     * Sets the persister for the scenario configuration.
     *
     * @param adapter the ScenarioRepositoryAdapter to use for persisting scenarios
     * @return the current instance of ScenarioConfigConfigurer
     */
    ScenarioConfigConfigurer<S> withPersister(ScenarioRepositoryAdapter<S> adapter);

    /**
     * Sets the id resolver for the scenario configuration.
     * <p>
     * {@code ScenarioIdResolver} serves to determine by which ID to try to find the scenario for
     * each {@link UpdateRequest}. It can be userId for example, or something else.
     * <p>
     * By default, used {@link SimpleScenarioIdResolver}, but if you want manually control the
     * behaviour you can set yours {@code ScenarioIdResolver}
     *
     * @param resolver the ScenarioIdResolver to use for resolving scenario IDs
     * @return the current instance of ScenarioConfigConfigurer
     */
    ScenarioConfigConfigurer<S> withIdResolver(ScenarioIdResolver resolver);
}
