package io.github.drednote.telegram.datasource.permission.jpa;

import io.github.drednote.telegram.datasource.permission.PermissionRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaPermissionRepository extends PermissionRepository<PermissionEntity> {
}
