package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;
import io.github.drednote.telegram.datasource.Permission;
import io.github.drednote.telegram.filter.PermissionProperties.Access;
import io.github.drednote.telegram.filter.PermissionProperties.Role;
import io.github.drednote.telegram.updatehandler.response.ForbiddenHandlerResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class AccessPermissionFilter implements PriorityUpdateFilter {

  private final PermissionProperties permissionProperties;

  @Override
  public void preFilter(@NonNull ExtendedTelegramUpdateRequest request) {
    if (permissionProperties.getAccess() == Access.BY_ROLE) {
      Permission permission = request.getPermission();
      boolean canRead = permission.getRoles().stream()
          .anyMatch(role -> Optional.ofNullable(permissionProperties.getRoles().get(role))
              .map(Role::isCanRead)
              .orElse(false));
      if (!canRead) {
        request.setResponse(ForbiddenHandlerResponse.INSTANCE);
      }
    }
  }

  @Override
  public boolean matches(ExtendedTelegramUpdateRequest request) {
    return request.getChat() != null;
  }

  @Override
  public final int getPreOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 101;
  }
}
