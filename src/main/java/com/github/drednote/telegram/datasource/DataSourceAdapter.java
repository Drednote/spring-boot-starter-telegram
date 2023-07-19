package com.github.drednote.telegram.datasource;

import org.springframework.data.repository.CrudRepository;

/**
 * @apiNote can be null, if no datasource configured
 */
public interface DataSourceAdapter {

  CrudRepository<? extends Permission, Long> getPermissionRepository();
}
