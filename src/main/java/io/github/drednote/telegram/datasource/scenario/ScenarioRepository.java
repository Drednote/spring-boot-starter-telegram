package io.github.drednote.telegram.datasource.scenario;

import io.github.drednote.telegram.handler.scenario.persist.ScenarioTransitionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ScenarioRepository<T extends ScenarioEntity> extends CrudRepository<T, String> {
}
