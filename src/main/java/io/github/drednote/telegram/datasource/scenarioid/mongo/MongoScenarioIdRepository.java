package io.github.drednote.telegram.datasource.scenarioid.mongo;

import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepository;
import io.github.drednote.telegram.datasource.scenarioid.jpa.ScenarioIdEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MongoScenarioIdRepository extends ScenarioIdRepository<ScenarioIdDocument>,
    MongoRepository<ScenarioIdDocument, String> {
}
