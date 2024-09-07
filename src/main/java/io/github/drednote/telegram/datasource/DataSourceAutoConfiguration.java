package io.github.drednote.telegram.datasource;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.datasource.permission.DefaultPermissionRepositoryAdapter;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.datasource.permission.PermissionRepository;
import io.github.drednote.telegram.datasource.permission.PermissionRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario_deprecated.DefaultScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario_deprecated.InMemoryScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario_deprecated.PersistScenario;
import io.github.drednote.telegram.datasource.scenario_deprecated.ScenarioRepository;
import io.github.drednote.telegram.datasource.scenario_deprecated.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioRepository;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioRepositoryAdapter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

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
