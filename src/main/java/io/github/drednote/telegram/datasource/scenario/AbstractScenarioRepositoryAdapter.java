package io.github.drednote.telegram.datasource.scenario;

import io.github.drednote.telegram.datasource.kryo.KryoSerializationService;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioContext;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractScenarioRepositoryAdapter<S> implements ScenarioRepositoryAdapter<S> {

    private final KryoSerializationService<ScenarioContext<S>> serializationService;

    protected abstract Optional<? extends ScenarioEntity> read(String id);

    protected abstract void write(ScenarioContext<S> persistContext, byte[] context);

    @Override
    public Optional<? extends ScenarioContext<S>> findById(String id) {
        return read(id).map(scenarioEntity -> {
            ScenarioContext<S> context = serializationService.deserialize(scenarioEntity.getContext());
            return new SimpleScenarioContext<>(
                scenarioEntity.getId(),
                context.state()
            );
        });
    }

    @Override
    public void save(ScenarioContext<S> persistContext) throws IOException {
        byte[] context = serializationService.serialize(persistContext);
        write(persistContext, context);
    }
}
