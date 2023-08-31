package io.github.drednote.telegram.filter.pre;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.datasource.DataSourceAdapter;
import io.github.drednote.telegram.datasource.Permission;
import io.github.drednote.telegram.datasource.Permission.DefaultPermission;
import io.github.drednote.telegram.filter.PermissionProperties;
import io.github.drednote.telegram.utils.Assert;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Implementation of a priority pre-update filter for assigning roles to users.
 *
 * <p>This filter assigns roles to a user by querying the {@link DataSourceAdapter} for permission
 * information and using the role assignment configuration from the {@link PermissionProperties}. If
 * no roles are assigned, it assigns the default role specified in the properties.
 *
 * @author Ivan Galushko
 * @see DataSourceAdapter
 * @see PermissionProperties
 */
public class RoleFilter implements PriorityPreUpdateFilter {

  private final ObjectProvider<DataSourceAdapter> adapterProvider;
  private final PermissionProperties permissionProperties;

  /**
   * Constructs a RoleFilter with the specified {@link DataSourceAdapter} provider and {@link
   * PermissionProperties}.
   *
   * @param adapterProvider      The provider for the DataSourceAdapter, not null
   * @param permissionProperties The permission properties for role assignment, not null
   * @throws IllegalArgumentException if either adapterProvider or permissionProperties is null
   */
  public RoleFilter(
      ObjectProvider<DataSourceAdapter> adapterProvider, PermissionProperties permissionProperties
  ) {
    Assert.required(adapterProvider, "DataSourceAdapter provider");
    Assert.required(permissionProperties, "PermissionProperties");

    this.adapterProvider = adapterProvider;
    this.permissionProperties = permissionProperties;
  }

  /**
   * Pre-filters the incoming Telegram update request to assign roles to the user.
   *
   * <p>This method assigns roles to a user by querying the {@link DataSourceAdapter} for
   * permission information and using the role assignment configuration from the {@link
   * PermissionProperties}. If no roles are assigned, it assigns the default role specified in the
   * properties.
   *
   * @param request The incoming Telegram update request to be pre-filtered, not null
   */
  @Override
  @SuppressWarnings({"java:S1874", "deprecation"})
  public void preFilter(@NonNull TelegramUpdateRequest request) {
    Assert.notNull(request, "TelegramUpdateRequest");

    User user = request.getUser();
    Set<String> roles = new HashSet<>();
    if (user != null) {
      Long id = user.getId();
      adapterProvider.ifAvailable(adapter -> {
        CrudRepository<? extends Permission, Long> repository = adapter.permissionRepository();
        repository.findById(id).ifPresent(permission -> roles.addAll(permission.getRoles()));
      });
      Optional.ofNullable(permissionProperties.getAssignRole().get(id)).ifPresent(roles::addAll);
    }

    if (roles.isEmpty()) {
      roles.add(permissionProperties.getDefaultRole());
    }

    request.setPermission(new DefaultPermission(roles));
  }

  @Override
  public boolean matches(TelegramUpdateRequest request) {
    return request.getChat() != null;
  }

  @Override
  public final int getPreOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 100;
  }
}
