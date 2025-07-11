package io.github.drednote.telegram.datasource.scenario;

import io.github.drednote.telegram.datasource.kryo.KryoSerializationService;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import java.io.IOException;
import java.util.Optional;

public abstract class AbstractScenarioRepositoryAdapter<S> implements ScenarioRepositoryAdapter<S> {

    private final KryoSerializationService<ScenarioContext<S>> serializationService;

    public AbstractScenarioRepositoryAdapter(KryoSerializationService<ScenarioContext<S>> serializationService) {
        this.serializationService = serializationService;
    }

    protected abstract Optional<? extends ScenarioEntity> read(String id);

    protected abstract void write(ScenarioContext<S> persistContext, byte[] context);

    @Override
    public Optional<? extends ScenarioContext<S>> findById(String id) {
        return read(id).map(scenarioEntity -> serializationService.deserialize(scenarioEntity.getContext()));
    }

    @Override
    public void save(ScenarioContext<S> persistContext) throws IOException {
        byte[] context = serializationService.serialize(persistContext);
        write(persistContext, context);
    }
}
