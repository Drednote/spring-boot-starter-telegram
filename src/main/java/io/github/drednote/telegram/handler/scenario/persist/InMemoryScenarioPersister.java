package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.handler.scenario.Scenario;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryScenarioPersister<S> implements ScenarioPersister<S> {

    private final Map<String, Scenario<S>> holder = new ConcurrentHashMap<>();

    @Override
    public void persist(Scenario<S> scenario) throws Exception {
        holder.put(scenario.getId(), scenario);
    }

    @Override
    public Scenario<S> restore(Scenario<S> scenario, String scenarioId) {
        return holder.getOrDefault(scenarioId, scenario);
    }
}