package io.github.drednote.telegram.datasource.scenarioid;

import io.github.drednote.telegram.datasource.DataSourceAdapter;
import java.util.Optional;

/**
 * Interface for adapting scenario ID repository interactions.
 *
 * @author Ivan Galushko
 */
public interface ScenarioIdRepositoryAdapter extends DataSourceAdapter {

    /**
     * Finds a scenario ID by userID.
     *
     * @param id the numeric identifier of the scenario ID to find
     * @return an Optional containing the found ScenarioId, or empty if not found
     */
    Optional<? extends ScenarioId> findById(Long id);

    /**
     * Saves the specified scenario ID with userID.
     *
     * @param scenarioId the scenario ID to be saved
     * @param id the numeric identifier associated with the scenario ID
     */
    void save(String scenarioId, Long id);
}
