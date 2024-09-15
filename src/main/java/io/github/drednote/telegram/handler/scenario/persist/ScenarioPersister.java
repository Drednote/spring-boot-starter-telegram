package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.handler.scenario.Scenario;

public interface ScenarioPersister<S> {

    void persist(Scenario<S> context);

    void changeId(Scenario<S> context, String newId);

    Scenario<S> restore(Scenario<S> scenario, String scenarioId);
}
