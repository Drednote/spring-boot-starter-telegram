package io.github.drednote.telegram.datasource.scenario;

import io.github.drednote.telegram.datasource.DataSourceAdapter;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioTransitionContext;
import java.io.IOException;
import java.util.Optional;

public interface ScenarioRepositoryAdapter<S> extends DataSourceAdapter {

    Optional<? extends ScenarioContext<S>> findById(String id);

    void save(ScenarioContext<S> persistContext) throws IOException;
}
