package com.github.drednote.telegram.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public class DataSourceAdapterImpl implements DataSourceAdapter {

  private final CrudRepository<? extends Permission, Long> permissionRepository;

  @Override
  @Nullable
  public CrudRepository<? extends Permission, Long> getPermissionRepository() {
    return permissionRepository;
  }
}
