package io.github.drednote.telegram.handler.scenario.factory;

import io.github.drednote.telegram.handler.scenario.Scenario;

/**
 * Interface for creating scenario instances.
 *
 * @param <S> the type of the scenario state
 * @author Ivan Galushko
 */
public interface ScenarioFactory<S> {

    /**
     * Creates a scenario instance with the given scenario ID.
     *
     * @param scenarioId the ID of the scenario to create
     * @return a newly created Scenario instance
     */
    Scenario<S> create(String scenarioId);
}
