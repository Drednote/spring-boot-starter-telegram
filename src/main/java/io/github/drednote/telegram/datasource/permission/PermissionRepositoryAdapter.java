package io.github.drednote.telegram.datasource.permission;

import io.github.drednote.telegram.datasource.DataSourceAdapter;
import org.springframework.lang.Nullable;

public interface PermissionRepositoryAdapter extends DataSourceAdapter {

  @Nullable
  Permission findPermission(Long chatId);
}
