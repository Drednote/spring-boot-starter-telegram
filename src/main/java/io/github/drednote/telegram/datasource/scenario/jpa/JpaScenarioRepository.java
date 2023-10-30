package io.github.drednote.telegram.datasource.scenario.jpa;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaScenarioRepository extends ScenarioRepository<ScenarioEntity> {
}
