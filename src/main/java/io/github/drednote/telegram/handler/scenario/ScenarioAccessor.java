package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;

public interface ScenarioAccessor<S> {

    void resetScenario(ScenarioContext<S> context);

    void setId(String id);

    ScenarioIdResolver getIdResolver();

    ScenarioPersister<S> getPersister();
}
