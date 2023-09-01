package io.github.drednote.telegram.filter.pre;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.datasource.Permission;
import io.github.drednote.telegram.filter.PermissionProperties;
import io.github.drednote.telegram.filter.PermissionProperties.Access;
import io.github.drednote.telegram.filter.PermissionProperties.Role;
import io.github.drednote.telegram.response.ForbiddenTelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import java.util.Optional;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * Implementation of a priority pre-update filter for managing access permissions.
 *
 * <p>This filter checks whether the access mode is set to {@link Access#BY_ROLE} in the permission
 * properties. If so, it verifies if the user has read access based on their roles and the
 * role-based access configuration in the permission properties.
 *
 * <p>If the user does not have the necessary read access, this filter sets the
 * {@link ForbiddenTelegramResponse} as the response for the update request.
 *
 * @author Ivan Galushko
 * @see PermissionProperties
 * @see Permission
 */
public class AccessPermissionFilter implements PriorityPreUpdateFilter {

  private final PermissionProperties permissionProperties;

  /**
   * Constructs a AccessPermissionFilter with the specified {@link PermissionProperties}.
   *
   * @param permissionProperties The permission properties for role assignment, not null
   * @throws IllegalArgumentException if either adapterProvider or permissionProperties is null
   */
  public AccessPermissionFilter(PermissionProperties permissionProperties) {
    Assert.required(permissionProperties, "PermissionProperties");
    this.permissionProperties = permissionProperties;
  }

  /**
   * Pre-filters the incoming Telegram update request to enforce access permissions.
   *
   * <p>This method checks whether the access mode is set to {@link Access#BY_ROLE} in the
   * permission properties. If so, it verifies if the user has read access based on their roles and
   * the role-based access configuration in the permission properties.
   *
   * <p>If the user does not have the necessary read access, this method sets the
   * {@link ForbiddenTelegramResponse} as the response for the update request.
   *
   * @param request The incoming Telegram update request to be pre-filtered, not null
   */
  @Override
  public void preFilter(@NonNull TelegramUpdateRequest request) {
    Assert.notNull(request, "TelegramUpdateRequest");
    if (permissionProperties.getAccess() == Access.BY_ROLE) {
      Permission permission = request.getPermission();
      Assert.notNull(permission, "Permission");
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
    Assert.notNull(request, "TelegramUpdateRequest");
    return request.getChat() != null;
  }

  @Override
  public final int getPreOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 101;
  }
}
