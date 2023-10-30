package io.github.drednote.telegram.datasource.scenario.mongo;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MongoScenarioRepository extends ScenarioRepository<ScenarioDocument> {
}
