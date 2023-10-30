package io.github.drednote.telegram.datasource.scenario;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ScenarioRepository<T extends PersistScenario> extends CrudRepository<T, Long> {
}
