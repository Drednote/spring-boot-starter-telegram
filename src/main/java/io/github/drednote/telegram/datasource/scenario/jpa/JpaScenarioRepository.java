package io.github.drednote.telegram.datasource.scenario.jpa;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaScenarioRepository extends ScenarioRepository<JpaScenarioEntity>,
    JpaRepository<JpaScenarioEntity, String> {
}
