package io.github.drednote.telegram.datasource.scenario_deprecated.mongo;

import io.github.drednote.telegram.datasource.scenario_deprecated.ScenarioRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MongoScenarioRepository extends ScenarioRepository<ScenarioDocument> {
}
