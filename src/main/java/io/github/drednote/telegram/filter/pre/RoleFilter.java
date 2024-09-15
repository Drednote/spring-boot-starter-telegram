package io.github.drednote.telegram.filter.pre;

import static io.github.drednote.telegram.filter.FilterOrder.DEFAULT_PRECEDENCE;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.datasource.permission.Permission.DefaultPermission;
import io.github.drednote.telegram.datasource.permission.PermissionRepositoryAdapter;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.filter.PermissionProperties;
import io.github.drednote.telegram.utils.Assert;
import io.github.drednote.telegram.utils.FieldProvider;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Implementation of a priority pre-update filter for assigning roles to users.
 *
 * <p>This filter assigns roles to a user by querying the {@link PermissionRepositoryAdapter} for
 * permission
 * information and using the role assignment configuration from the {@link PermissionProperties}. If
 * no roles are assigned, it assigns the default role specified in the properties.
 *
 * @author Ivan Galushko
 * @see PermissionRepositoryAdapter
 * @see PermissionProperties
 */
public class RoleFilter implements PriorityPreUpdateFilter {

  private final FieldProvider<PermissionRepositoryAdapter> permissionRepositoryAdapter;
  private final PermissionProperties permissionProperties;

  /**
   * Constructs a RoleFilter with the specified {@link PermissionRepositoryAdapter} and
   * {@link PermissionProperties}.
   *
   * @param permissionRepositoryAdapter The provider for the Permission, not null
   * @param permissionProperties        The permission properties for role assignment, not null
   * @throws IllegalArgumentException if either permissionRepositoryAdapter or permissionProperties
   *                                  is null
   */
  public RoleFilter(
      FieldProvider<PermissionRepositoryAdapter> permissionRepositoryAdapter,
      PermissionProperties permissionProperties
  ) {
    Assert.required(permissionRepositoryAdapter, "PermissionRepositoryAdapter");
    Assert.required(permissionProperties, "PermissionProperties");

    this.permissionRepositoryAdapter = permissionRepositoryAdapter;
    this.permissionProperties = permissionProperties;
  }

  /**
   * Pre-filters the incoming Telegram update request to assign roles to the user.
   *
   * <p>This method assigns roles to a user by querying the {@link PermissionRepositoryAdapter} for
   * permission information and using the role assignment configuration from the
   * {@link PermissionProperties}. If no roles are assigned, it assigns the default role specified
   * in the properties.
   *
   * @param request The incoming Telegram update request to be pre-filtered, not null
   */
  @Override
  @SuppressWarnings({"java:S1874", "deprecation"})
  public void preFilter(@NonNull UpdateRequest request) {
    Assert.notNull(request, "UpdateRequest");

    User user = request.getUser();
    Set<String> roles = new HashSet<>();
    if (user != null) {
      Long id = user.getId();

      permissionRepositoryAdapter.ifExists(adapter -> {
        Permission permission = adapter.findPermission(id);
        if (permission != null && permission.getRoles() != null) {
          roles.addAll(permission.getRoles());
        }
      });

      Optional.ofNullable(permissionProperties.getAssignRole().get(id)).ifPresent(roles::addAll);
    }

    if (roles.isEmpty()) {
      roles.add(permissionProperties.getDefaultRole());
    }

    request.getAccessor().setPermission(new DefaultPermission(request.getChatId(), roles));
  }

  @Override
  public int getPreOrder() {
    return FilterOrder.PRIORITY_PRE_FILTERS.get(this.getClass());
  }
}
