package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;

public interface ScenarioAccessor<S> {

    void resetScenario(ScenarioContext<S> context);
}
