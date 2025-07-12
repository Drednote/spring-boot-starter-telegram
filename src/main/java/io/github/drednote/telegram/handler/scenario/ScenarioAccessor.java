package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import org.springframework.lang.Nullable;

/**
 * Provides access to manipulate and retrieve details of a scenario.
 *
 * @param <S> the type of the state managed by the scenario
 * @author Ivan Galushko
 */
public interface ScenarioAccessor<S> {

    /**
     * Resets the scenario with the specified context.
     *
     * @param context the context to reset the scenario with
     */
    void resetScenario(ScenarioContext<S> context);

    /**
     * Sets the unique identifier for the scenario.
     *
     * @param id the unique identifier as a String
     */
    void setId(String id);

    /**
     * Add property to the scenario instance. Usually properties need to mark the scenario instance with the execution
     * result or some additional config.
     *
     * @param key key
     * @param value value
     */
    void addProperty(String key, @Nullable Object value);

    /**
     * Retrieves the scenario ID resolver.
     *
     * @return an instance of ScenarioIdResolver used for resolving scenario IDs
     */
    ScenarioIdResolver getIdResolver();

    /**
     * Retrieves the scenario persister.
     *
     * @return an instance of {@code ScenarioPersister<S>} for persisting scenario state
     */
    ScenarioPersister<S> getPersister();
}

