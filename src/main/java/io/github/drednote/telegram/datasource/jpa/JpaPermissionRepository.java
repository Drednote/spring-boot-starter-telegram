package io.github.drednote.telegram.datasource.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, Long> {
}
