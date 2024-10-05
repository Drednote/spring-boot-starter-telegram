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
     * @param context the scenario context to be persisted
     */
    void persist(Scenario<S> context);

    /**
     * Restores a scenario from a persisted state using the provided scenario ID.
     *
     * @param scenario  the scenario to restore data into
     * @param scenarioId the ID of the scenario to restore
     */
    void restore(Scenario<S> scenario, String scenarioId);
}

