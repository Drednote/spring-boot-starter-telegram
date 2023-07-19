package com.github.drednote.telegram.datasource;

import com.github.drednote.telegram.datasource.jpa.PermissionEntity;
import com.github.drednote.telegram.datasource.jpa.PermissionRepository;
import com.github.drednote.telegram.datasource.mongo.MongoPermissionRepository;
import jakarta.persistence.EntityManager;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
public class DataSourceAutoConfiguration {

  @ConditionalOnClass(JpaRepository.class)
  @EntityScan(basePackageClasses = PermissionRepository.class)
  @EnableJpaRepositories(basePackageClasses = PermissionRepository.class)
  @AutoConfiguration
  @Order(0)
  static class Jpa {

    @Bean
    @ConditionalOnMissingBean
    public DataSourceAdapter dataSourceAdapter(PermissionRepository repository) {
      return new DataSourceAdapterImpl(repository);
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
                .generate(Set.of(PermissionEntity.class));
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
    public DataSourceAdapter dataSourceAdapter(MongoPermissionRepository repository) {
      return new DataSourceAdapterImpl(repository);
    }
  }
}
