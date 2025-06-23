package io.github.drednote.telegram.datasource;

import io.github.drednote.telegram.datasource.permission.DefaultPermissionRepositoryAdapter;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.datasource.permission.PermissionRepository;
import io.github.drednote.telegram.datasource.permission.PermissionRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.jpa.JpaScenarioIdRepository;
import io.github.drednote.telegram.datasource.scenarioid.jpa.JpaScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.datasource.session.UpdateInboxRepositoryAdapter;
import io.github.drednote.telegram.datasource.session.jpa.JpaUpdateInboxRepository;
import io.github.drednote.telegram.datasource.session.jpa.JpaUpdateInboxRepositoryAdapter;
import io.github.drednote.telegram.datasource.session.jpa.PostgresUpdateInboxRepositoryAdapter;
import io.github.drednote.telegram.session.SessionProperties;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

@AutoConfiguration
public class DataSourceAutoConfiguration {

    @AutoConfiguration
    @ConditionalOnClass(JpaRepository.class)
    public static class JpaAutoConfiguration {

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

        @AutoConfiguration
        @ConditionalOnExpression(
            value = "#{environment.getProperty('drednote.telegram.session.update-processor-type')?.equalsIgnoreCase('SCHEDULER_WITH_CRUD')}"
        )
        public static class JpaUpdateInboxAutoConfiguration {

            @Bean
            @ConditionalOnBean(JpaUpdateInboxRepository.class)
            @ConditionalOnMissingBean({UpdateInboxRepositoryAdapter.class})
            @Conditional(PostgresCondition.class)
            public UpdateInboxRepositoryAdapter<?> postgresUpdateInboxRepositoryAdapter(
                JpaUpdateInboxRepository repository, EntityManager entityManager, SessionProperties sessionProperties
            ) {
                return new PostgresUpdateInboxRepositoryAdapter(repository, entityManager, sessionProperties);
            }

            @Bean
            @ConditionalOnBean(JpaUpdateInboxRepository.class)
            @ConditionalOnMissingBean({UpdateInboxRepositoryAdapter.class})
            @Conditional(NotPostgresCondition.class)
            public UpdateInboxRepositoryAdapter<?> jpaUpdateInboxRepositoryAdapter(
                JpaUpdateInboxRepository repository, EntityManager entityManager, SessionProperties sessionProperties
            ) {
                return new JpaUpdateInboxRepositoryAdapter(repository, entityManager, sessionProperties);
            }

            static class NotPostgresCondition implements Condition {

                private final PostgresCondition condition = new PostgresCondition();

                @Override
                public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
                    return !condition.matches(context, metadata);
                }
            }

            static class PostgresCondition implements Condition {

                private static final String KEY = "spring.jpa.database";

                @Override
                public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
                    String dbType = context.getEnvironment().getProperty(KEY);
                    return "postgresql".equalsIgnoreCase(dbType);
                }
            }
        }
    }
}
