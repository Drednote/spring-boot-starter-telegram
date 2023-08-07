package io.github.drednote.telegram.datasource.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoPermissionRepository extends MongoRepository<PermissionDocument, Long> {
}
