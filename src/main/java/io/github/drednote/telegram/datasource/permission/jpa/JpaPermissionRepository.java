package io.github.drednote.telegram.datasource.permission.jpa;

import io.github.drednote.telegram.datasource.permission.PermissionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaPermissionRepository extends PermissionRepository<PermissionEntity>,
    JpaRepository<PermissionEntity, Long> {
}
