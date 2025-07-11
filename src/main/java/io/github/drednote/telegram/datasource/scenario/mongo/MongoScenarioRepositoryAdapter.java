package io.github.drednote.telegram.datasource.scenario.mongo;

import io.github.drednote.telegram.datasource.kryo.KryoSerializationService;
import io.github.drednote.telegram.datasource.scenario.AbstractScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.ScenarioEntity;
import io.github.drednote.telegram.datasource.scenario.ScenarioKryoSerializationService;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import java.util.Optional;

public class MongoScenarioRepositoryAdapter<S> extends AbstractScenarioRepositoryAdapter<S> {

    private final MongoScenarioRepository mongoScenarioRepository;

    public MongoScenarioRepositoryAdapter(MongoScenarioRepository scenarioRepository) {
        this(new ScenarioKryoSerializationService<>(), scenarioRepository);
    }

    public MongoScenarioRepositoryAdapter(
        KryoSerializationService<ScenarioContext<S>> serializationService,
        MongoScenarioRepository mongoScenarioRepository
    ) {
        super(serializationService);
        this.mongoScenarioRepository = mongoScenarioRepository;
    }

    @Override
    protected Optional<? extends ScenarioEntity> read(String id) {
        return mongoScenarioRepository.findById(id);
    }

    @Override
    protected void write(ScenarioContext<S> persistContext, byte[] context) {
        mongoScenarioRepository.save(convert(persistContext, context));
    }

    private MongoScenarioDocument convert(ScenarioContext<S> persistContext, byte[] context) {
        MongoScenarioDocument transitionContext = new MongoScenarioDocument();
        transitionContext.setId(persistContext.getId());
        transitionContext.setState(persistContext.getMachine().getState().toString());
        transitionContext.setContext(context);
        return transitionContext;
    }
}
