package io.github.drednote.telegram.datasource.scenarioid;

import io.github.drednote.telegram.datasource.DataSourceAdapter;
import java.util.Optional;

public interface ScenarioIdRepositoryAdapter extends DataSourceAdapter {

    Optional<? extends ScenarioId> findById(Long id);

    void save(String scenarioId, Long id);
}
