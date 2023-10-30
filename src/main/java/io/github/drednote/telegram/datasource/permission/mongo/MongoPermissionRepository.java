package io.github.drednote.telegram.datasource.permission.mongo;

import io.github.drednote.telegram.datasource.permission.PermissionRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MongoPermissionRepository extends PermissionRepository<PermissionDocument> {
}
