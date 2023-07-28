package com.github.drednote.telegram.datasource.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaScenarioRepository extends JpaRepository<ScenarioEntity, Long> {
}
