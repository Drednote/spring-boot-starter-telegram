package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.datasource.DataSourceAdapter;
import com.github.drednote.telegram.datasource.Permission;
import com.github.drednote.telegram.datasource.Permission.DefaultPermission;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.User;

@RequiredArgsConstructor
public class RoleUpdateFilter implements UpdateFilter {

  @Nullable
  private final DataSourceAdapter adapter;
  private final PermissionProperties permissionProperties;

  @Override
  public void filter(UpdateRequest request) {
    User user = request.getUser();
    Set<String> roles = new HashSet<>();
    if (user != null) {
      Long id = user.getId();
      if (adapter != null) {
        CrudRepository<? extends Permission, Long> repository = adapter.getPermissionRepository();
        repository.findById(id).ifPresent(permission -> roles.addAll(permission.getRoles()));
      }
      Optional.ofNullable(permissionProperties.getAssignRole().get(id)).ifPresent(roles::addAll);
    }

    if (roles.isEmpty()) {
      roles.add(permissionProperties.getDefaultRole());
    }

    request.setPermission(new DefaultPermission(roles));
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 100;
  }
}
