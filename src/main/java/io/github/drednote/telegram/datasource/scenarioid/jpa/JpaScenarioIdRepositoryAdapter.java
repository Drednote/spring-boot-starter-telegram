package io.github.drednote.telegram.datasource.scenarioid.jpa;

import io.github.drednote.telegram.datasource.scenarioid.ScenarioId;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepositoryAdapter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaScenarioIdRepositoryAdapter implements ScenarioIdRepositoryAdapter {

    private final JpaScenarioIdRepository scenarioIdRepository;

    @Override
    public Optional<? extends ScenarioId> findById(Long id) {
        return scenarioIdRepository.findById(id);
    }

    @Override
    public void save(String scenarioId, Long id) {
        ScenarioIdEntity entity = new ScenarioIdEntity();
        entity.setId(id);
        entity.setScenarioId(scenarioId);
        scenarioIdRepository.save(entity);
    }
}
