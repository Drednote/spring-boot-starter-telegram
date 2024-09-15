package io.github.drednote.telegram.datasource;

import io.github.drednote.telegram.datasource.permission.DefaultPermissionRepositoryAdapter;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.datasource.permission.PermissionRepository;
import io.github.drednote.telegram.datasource.permission.PermissionRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.jpa.JpaScenarioIdRepository;
import io.github.drednote.telegram.datasource.scenarioid.jpa.JpaScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepositoryAdapter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;

@AutoConfiguration
public class DataSourceAutoConfiguration {

    @AutoConfiguration
    @ConditionalOnClass(CrudRepository.class)
    public static class CrudAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(PermissionRepositoryAdapter.class)
        @ConditionalOnBean(PermissionRepository.class)
        public PermissionRepositoryAdapter permissionAdapter(
            PermissionRepository<? extends Permission> permissionRepository
        ) {
            return new DefaultPermissionRepositoryAdapter(permissionRepository);
        }

        @Bean
        @ConditionalOnMissingBean(ScenarioIdRepositoryAdapter.class)
        @ConditionalOnBean(JpaScenarioIdRepository.class)
        public ScenarioIdRepositoryAdapter scenarioIdRepositoryAdapter(
            JpaScenarioIdRepository scenarioIdRepository
        ) {
            return new JpaScenarioIdRepositoryAdapter(scenarioIdRepository);
        }
    }
}
