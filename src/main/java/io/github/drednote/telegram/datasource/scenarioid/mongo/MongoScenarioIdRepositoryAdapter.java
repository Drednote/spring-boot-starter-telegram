package io.github.drednote.telegram.datasource.scenarioid.mongo;

import io.github.drednote.telegram.datasource.scenarioid.ScenarioId;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.utils.Assert;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

public class MongoScenarioIdRepositoryAdapter implements ScenarioIdRepositoryAdapter {

    private final MongoScenarioIdRepository scenarioIdRepository;

    public MongoScenarioIdRepositoryAdapter(MongoScenarioIdRepository scenarioIdRepository) {
        Assert.required(scenarioIdRepository, "MongoScenarioIdRepository");
        this.scenarioIdRepository = scenarioIdRepository;
    }

    @Override
    public Optional<? extends ScenarioId> findById(String id) {
        return scenarioIdRepository.findById(id);
    }

    @Override
    public void save(String scenarioId, String id) {
        ScenarioIdDocument entity = new ScenarioIdDocument();
        entity.setId(id);
        entity.setScenarioId(scenarioId);
        scenarioIdRepository.save(entity);
    }
}
