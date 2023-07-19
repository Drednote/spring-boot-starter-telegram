package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.datasource.Permission;
import com.github.drednote.telegram.filter.PermissionProperties.Access;
import com.github.drednote.telegram.filter.PermissionProperties.Role;
import com.github.drednote.telegram.updatehandler.response.ForbiddenHandlerResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;

@RequiredArgsConstructor
public class AccessPermissionUpdateFilter implements UpdateFilter {

  private final PermissionProperties permissionProperties;

  @Override
  public void filter(UpdateRequest request) {
    if (permissionProperties.getAccess() == Access.BY_ROLE) {
      Permission permission = request.getPermission();
      boolean canRead = permission.getRoles().stream()
          .anyMatch(role -> Optional.ofNullable(permissionProperties.getRoles().get(role))
              .map(Role::isCanRead)
              .orElse(false));
      if (!canRead) {
        request.setResponse(new ForbiddenHandlerResponse());
      }
    }
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 101;
  }
}
