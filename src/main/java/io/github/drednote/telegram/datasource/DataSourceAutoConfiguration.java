package io.github.drednote.telegram.datasource;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.datasource.permission.DefaultPermissionRepositoryAdapter;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.datasource.permission.PermissionRepositoryAdapter;
import io.github.drednote.telegram.datasource.permission.PermissionRepository;
import io.github.drednote.telegram.datasource.scenario.DefaultScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.InMemoryScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.PersistScenario;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@BetaApi
public class DataSourceAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(PermissionRepositoryAdapter.class)
  public PermissionRepositoryAdapter permissionAdapter(
      ObjectProvider<PermissionRepository<? extends Permission>> permissionRepository
  ) {
    return new DefaultPermissionRepositoryAdapter(permissionRepository);
  }

  @Bean
  @ConditionalOnMissingBean(ScenarioRepositoryAdapter.class)
  public ScenarioRepositoryAdapter persistScenarioAdapter(
      ObjectProvider<ScenarioRepository<? extends PersistScenario>> repository
  ) {
    return new DefaultScenarioRepositoryAdapter(repository, new InMemoryScenarioRepositoryAdapter());
  }
}
