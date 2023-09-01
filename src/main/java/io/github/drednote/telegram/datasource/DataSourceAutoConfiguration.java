package io.github.drednote.telegram.datasource;

import io.github.drednote.telegram.datasource.jpa.JpaPermissionRepository;
import io.github.drednote.telegram.datasource.jpa.JpaScenarioRepository;
import io.github.drednote.telegram.datasource.jpa.PermissionEntity;
import io.github.drednote.telegram.datasource.jpa.ScenarioEntity;
import io.github.drednote.telegram.datasource.mongo.MongoPermissionRepository;
import io.github.drednote.telegram.datasource.mongo.MongoScenarioRepository;
import jakarta.persistence.EntityManager;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@AutoConfiguration
@EnableConfigurationProperties(DataSourceProperties.class)
@ConditionalOnProperty(
    prefix = "drednote.telegram.datasource",
    value = "disable-data-source-auto-configuration",
    havingValue = "false",
    matchIfMissing = true
)
public class DataSourceAutoConfiguration {

  @ConditionalOnClass(JpaRepository.class)
  @EntityScan(basePackageClasses = JpaPermissionRepository.class)
  @EnableJpaRepositories(basePackageClasses = JpaPermissionRepository.class)
  @AutoConfiguration
  @Order(0)
  static class Jpa {

    @Bean
    @ConditionalOnMissingBean
    public DataSourceAdapter dataSourceAdapter(
        JpaPermissionRepository repository, JpaScenarioRepository scenarioRepository
    ) {
      return new DataSourceAdapterImpl(repository, scenarioRepository);
    }

    @ConditionalOnClass(Hibernate.class)
    @AutoConfiguration
    static class HibernateConfig {

      public HibernateConfig(
          EntityManager entityManager, DataSource dataSource, DataSourceProperties properties
      ) {
        if (!properties.isDisableAutoGenerateTables()) {
          try {
            new SchemaGenerator(entityManager, dataSource)
                .generate(Set.of(PermissionEntity.class, ScenarioEntity.class));
          } catch (SQLException e) {
            throw new BeanCreationException("Cannot create tables", e);
          }
        }
      }
    }
  }

  @ConditionalOnClass(MongoRepository.class)
  @EntityScan(basePackageClasses = MongoPermissionRepository.class)
  @EnableMongoRepositories(basePackageClasses = MongoPermissionRepository.class)
  @AutoConfiguration
  @Order(1)
  static class Mongo {

    @Bean
    @ConditionalOnMissingBean
    public DataSourceAdapter dataSourceAdapter(
        MongoPermissionRepository repository, MongoScenarioRepository scenarioRepository
    ) {
      return new DataSourceAdapterImpl(repository, scenarioRepository);
    }
  }
}
