package io.github.drednote.telegram.datasource.scenarioid;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ScenarioIdRepository<T extends ScenarioId> extends CrudRepository<T, String> {
}
