package io.github.drednote.telegram.datasource.permission;

import io.github.drednote.telegram.utils.Assert;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.Nullable;

public class DefaultPermissionRepositoryAdapter implements PermissionRepositoryAdapter {

  @Nullable
  private final PermissionRepository<? extends Permission> repository;

  public DefaultPermissionRepositoryAdapter(
      ObjectProvider<PermissionRepository<? extends Permission>> permissionRepository
  ) {
    Assert.required(permissionRepository, "PermissionRepository provider");
    this.repository = permissionRepository.getIfAvailable();
  }

  @Override
  @Nullable
  public Permission findPermission(Long chatId) {
    return repository != null ? repository.findById(chatId).orElse(null) : null;
  }
}
