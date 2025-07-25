package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.handler.scenario.Scenario;

/**
 * Interface for persisting and restoring scenario instances.
 *
 * @param <S> the type of the scenario state
 * @author Ivan Galushko
 */
public interface ScenarioPersister<S> {

    /**
     * Persists the provided scenario context.
     *
     * @param scenario the scenario context to be persisted
     */
    void persist(Scenario<S> scenario) throws Exception;

    /**
     * Restores a scenario from a persisted state using the provided scenario ID.
     *
     * @param scenario  the scenario to restore data into
     * @param scenarioId the ID of the scenario to restore
     */
    Scenario<S> restore(Scenario<S> scenario, String scenarioId);
}

