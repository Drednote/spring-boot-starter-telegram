package io.github.drednote.telegram.datasource.scenarioid.jpa;

import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaScenarioIdRepository extends ScenarioIdRepository<ScenarioIdEntity> {
}
