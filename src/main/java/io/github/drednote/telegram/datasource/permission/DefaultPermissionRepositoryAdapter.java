package io.github.drednote.telegram.datasource.permission;

import io.github.drednote.telegram.utils.Assert;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.Nullable;

public class DefaultPermissionRepositoryAdapter implements PermissionRepositoryAdapter {

  private final PermissionRepository<? extends Permission> repository;

  public DefaultPermissionRepositoryAdapter(
      PermissionRepository<? extends Permission> permissionRepository
  ) {
    Assert.required(permissionRepository, "PermissionRepository");
    this.repository = permissionRepository;
  }

  @Override
  @Nullable
  public Permission findPermission(Long chatId) {
    return repository.findById(chatId).orElse(null);
  }
}
