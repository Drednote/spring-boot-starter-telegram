package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.handler.scenario.Scenario;

public interface ScenarioFactory<S> {

    Scenario<S> create(String scenarioId);

}
