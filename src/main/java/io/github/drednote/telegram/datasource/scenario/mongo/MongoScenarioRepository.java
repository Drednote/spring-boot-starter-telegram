package io.github.drednote.telegram.datasource.scenario.mongo;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepository;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MongoScenarioRepository extends ScenarioRepository<MongoScenarioDocument>,
    MongoRepository<MongoScenarioDocument, String> {
}
