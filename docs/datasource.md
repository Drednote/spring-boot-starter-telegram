## Datasource

- For some filters and scenarios to work correctly, you need to save information to the database.
  You can save data to the application memory, but then during the restart, all information will be
  lost. Therefore, it is better if you configure the datasource using spring.

- This library is fully based on Spring JPA in working with the database. Therefore, to support
  different databases (postgres, mongo, etc.), using the implementations of `DataSourceAdapter`
  interface
- If you want to add support for a database that currently is not supported, you should to
  create entity and create repository extending `PermissionRepository`, `ScenarioRepository`
  or `ScenarioIdRepository`

> **Currently supported `JpaRepository`**

> Note: To enable auto scan for jpa entities, you should manually pick main interfaces for entities
> and use `@EntityScan` annotation. To create spring data repository, you need to just implement one
> of the repository interfaces

```java

@EntityScan(basePackageClasses = {Permission.class, PersistScenario.class})
@Configuration
public class JpaConfig {

}
```

```java

public interface PermissionRepository extends JpaPermissionRepository {}
```