package io.github.drednote.telegram.datasource.scenario.jpa;

import io.github.drednote.telegram.datasource.kryo.KryoSerializationService;
import io.github.drednote.telegram.datasource.scenario.AbstractScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.ScenarioEntity;
import io.github.drednote.telegram.datasource.scenario.ScenarioKryoSerializationService;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import java.io.IOException;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public class JpaScenarioRepositoryAdapter<S> extends AbstractScenarioRepositoryAdapter<S> {

    private final JpaScenarioRepository jpaScenarioRepository;

    public JpaScenarioRepositoryAdapter(JpaScenarioRepository scenarioRepository) {
        this(new ScenarioKryoSerializationService<>(), scenarioRepository);
    }

    public JpaScenarioRepositoryAdapter(
        KryoSerializationService<ScenarioContext<S>> serializationService,
        JpaScenarioRepository jpaScenarioRepository
    ) {
        super(serializationService);
        this.jpaScenarioRepository = jpaScenarioRepository;
    }

    @Override
    protected Optional<? extends ScenarioEntity> read(String id) {
        return jpaScenarioRepository.findById(id);
    }

    @Override
    protected void write(ScenarioContext<S> persistContext, byte[] context) {
        jpaScenarioRepository.save(convert(persistContext, context));
    }

    private JpaScenarioEntity convert(ScenarioContext<S> persistContext, byte[] context) {
        JpaScenarioEntity transitionContext = new JpaScenarioEntity();
        transitionContext.setId(persistContext.id());
        transitionContext.setState(persistContext.state().id().toString());
        transitionContext.setContext(context);
        return transitionContext;
    }

    @Override
    @Transactional
    public void changeId(ScenarioContext<S> context, String id) throws IOException {
        jpaScenarioRepository.deleteById(id);
        save(context);
    }
}
