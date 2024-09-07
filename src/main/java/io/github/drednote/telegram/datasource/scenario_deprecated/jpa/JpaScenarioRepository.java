package io.github.drednote.telegram.datasource.scenario_deprecated.jpa;

import io.github.drednote.telegram.datasource.scenario_deprecated.ScenarioRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaScenarioRepository extends ScenarioRepository<ScenarioEntity> {
}
