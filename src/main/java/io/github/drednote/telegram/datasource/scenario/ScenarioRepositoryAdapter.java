package io.github.drednote.telegram.datasource.scenario;

import io.github.drednote.telegram.datasource.DataSourceAdapter;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import java.io.IOException;
import java.util.Optional;

/**
 * Interface for adapting scenario repository interactions.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioRepositoryAdapter<S> extends DataSourceAdapter {

    /**
     * Finds a scenario context by its identifier.
     *
     * @param id the identifier of the scenario to find
     * @return an Optional containing the found ScenarioContext, or empty if not found
     */
    Optional<? extends ScenarioContext<S>> findById(String id);

    /**
     * Saves the specified scenario context.
     *
     * @param persistContext the scenario context to be saved
     * @throws IOException if an I/O error occurs during the operation
     */
    void save(ScenarioContext<S> persistContext) throws IOException;
}