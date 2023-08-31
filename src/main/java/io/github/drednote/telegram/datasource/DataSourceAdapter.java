package io.github.drednote.telegram.datasource;

import io.github.drednote.telegram.core.annotation.BetaApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.repository.CrudRepository;

/**
 * @apiNote this bean will not be present on context if no datasource configured, so use
 * {@link ObjectProvider} to inject it
 */
@BetaApi
public interface DataSourceAdapter {

  CrudRepository<? extends Permission, Long> permissionRepository();

  CrudRepository<? extends ScenarioDB, Long> scenarioRepository();
}
