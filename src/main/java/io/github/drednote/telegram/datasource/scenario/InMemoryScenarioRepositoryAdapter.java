package io.github.drednote.telegram.datasource.scenario;

import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryScenarioRepositoryAdapter<S> implements ScenarioRepositoryAdapter<S> {

    private final Map<String, ScenarioContext<S>> holder = new ConcurrentHashMap<>();

    @Override
    public Optional<? extends ScenarioContext<S>> findById(String id) {
        return Optional.ofNullable(holder.get(id));
    }

    @Override
    public void save(ScenarioContext<S> persistContext) throws IOException {
        holder.put(persistContext.getId(), persistContext);
    }
}
