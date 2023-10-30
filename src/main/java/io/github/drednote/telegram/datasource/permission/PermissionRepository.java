package io.github.drednote.telegram.datasource.permission;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PermissionRepository<T extends Permission> extends CrudRepository<T, Long> {
}
