package io.github.drednote.telegram.datasource.mongo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MongoScenarioRepository extends JpaRepository<ScenarioDocument, Long> {
}
