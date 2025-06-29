package io.github.drednote.telegram.datasource.scenarioid;

import io.github.drednote.telegram.datasource.scenarioid.ScenarioId.DefaultScenarioId;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryScenarioIdRepositoryAdapter implements ScenarioIdRepositoryAdapter {

    private final Map<String, ScenarioId> holder = new ConcurrentHashMap<>();

    @Override
    public Optional<? extends ScenarioId> findById(String id) {
        return Optional.ofNullable(holder.get(id));
    }

    @Override
    public void save(String scenarioId, String id) {
        holder.put(id, new DefaultScenarioId(id, scenarioId));
    }
}
