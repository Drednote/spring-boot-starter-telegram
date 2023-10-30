package io.github.drednote.telegram.datasource.permission;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.datasource.DataSourceAdapter;
import org.springframework.lang.Nullable;

@BetaApi
public interface PermissionRepositoryAdapter extends DataSourceAdapter {

  @Nullable
  Permission findPermission(Long chatId);
}
