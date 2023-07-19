package com.github.drednote.telegram.datasource;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;

/**
 * Create schemas for entities define by {@link Entity}
 */
@RequiredArgsConstructor
public class SchemaGenerator {

  private final EntityManager entityManager;
  private final DataSource dataSource;

  public void generate(Set<Class<?>> entities) throws SQLException {
    EntityManagerFactory factory = entityManager.getEntityManagerFactory();
    Map<String, Object> properties = new HashMap<>(factory.getProperties());

    try (Connection connection = dataSource.getConnection()) {
      for (Class<?> entity : entities) {
        String tableName = getTableName(entityManager, entity);
        ResultSet tables = connection.getMetaData()
            .getTables(null, null, tableName, null);
        doGenerate(properties, entity, !tables.next());
      }
    }
  }

  private void doGenerate(Map<String, Object> properties, Class<?> entity, boolean createScheme) {
    try (
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .applySettings(properties)
            .build()
    ) {
      MetadataSources metadataSources = new MetadataSources(registry);
      metadataSources.addAnnotatedClass(entity);
      Metadata metadata = metadataSources.buildMetadata();
      if (createScheme) {
        create(metadata, properties, registry);
      } else {
        update(metadata, properties, registry);
      }
    }
  }

  private void create(
      Metadata metadata, Map<String, Object> properties, ServiceRegistry serviceRegistry
  ) {
    properties.put(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.CREATE_ONLY);
    properties.put(AvailableSettings.JAKARTA_HBM2DDL_SCRIPTS_ACTION, Action.NONE);
    properties.put(AvailableSettings.JAKARTA_HBM2DDL_CREATE_SCHEMAS, true);
    SchemaManagementToolCoordinator.process(
        metadata,
        serviceRegistry,
        properties,
        action -> {
        }
    );
  }

  private void update(
      Metadata metadata, Map<String, Object> properties, ServiceRegistry serviceRegistry
  ) {
    properties.put(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.UPDATE);
    properties.put(AvailableSettings.JAKARTA_HBM2DDL_SCRIPTS_ACTION, Action.NONE);
    properties.put(AvailableSettings.JAKARTA_HBM2DDL_CREATE_SCHEMAS, false);
    SchemaManagementToolCoordinator.process(
        metadata,
        serviceRegistry,
        properties,
        action -> {
        }
    );
  }

  /**
   * Returns the table name for a given entity type in the {@link EntityManager}.
   */
  private static <T> String getTableName(EntityManager em, Class<T> entityClass) {
    /*
     * Check if the specified class is present in the metamodel.
     * Throws IllegalArgumentException if not.
     */
    Metamodel meta = em.getMetamodel();
    EntityType<T> entityType = meta.entity(entityClass);

    Table t = entityClass.getAnnotation(Table.class);

    return (t == null)
        ? entityType.getName().toUpperCase()
        : t.name();
  }
}