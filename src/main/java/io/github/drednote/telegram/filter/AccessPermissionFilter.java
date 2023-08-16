package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.datasource.Permission;
import io.github.drednote.telegram.filter.PermissionProperties.Access;
import io.github.drednote.telegram.filter.PermissionProperties.Role;
import io.github.drednote.telegram.updatehandler.response.ForbiddenTelegramResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class AccessPermissionFilter implements PriorityPreUpdateFilter {

  private final PermissionProperties permissionProperties;

  @Override
  public void preFilter(@NonNull TelegramUpdateRequest request) {
    if (permissionProperties.getAccess() == Access.BY_ROLE) {
      Permission permission = request.getPermission();
      boolean canRead = permission.getRoles().stream()
          .anyMatch(role -> Optional.ofNullable(permissionProperties.getRoles().get(role))
              .map(Role::isCanRead)
              .orElse(false));
      if (!canRead) {
        request.setResponse(ForbiddenTelegramResponse.INSTANCE);
      }
    }
  }

  @Override
  public boolean matches(TelegramUpdateRequest request) {
    return request.getChat() != null;
  }

  @Override
  public final int getPreOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 101;
  }
}
