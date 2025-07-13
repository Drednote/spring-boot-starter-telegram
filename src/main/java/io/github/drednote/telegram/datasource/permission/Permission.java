package io.github.drednote.telegram.datasource.permission;

import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import java.util.Set;
import org.springframework.lang.Nullable;

/**
 * Represents a permission model that contains a unique identifier and a set of roles.
 * <p>
 * This abstraction is typically used to describe the roles associated with a specific user or context in the request
 * pipeline.
 * </p>
 *
 * <p>Implementations may vary, but the typical usage includes associating permissions
 * with access control filters such as {@link PreUpdateFilter}.</p>
 *
 * @author Ivan Galushko
 */
public interface Permission {

    /**
     * Returns the unique identifier of the permission object, typically associated with a user or session.
     *
     * @return the permission ID
     */
    Long getId();

    /**
     * Returns the set of roles associated with this permission.
     * <p>
     * Can return {@code null} if no roles are assigned.
     * </p>
     *
     * @return a set of role names or {@code null}
     */
    @Nullable
    Set<String> getRoles();

    /**
     * Default implementation of {@link Permission} as an immutable Java record.
     * <p>
     * Provides a simple container for permission data with {@code id} and {@code roles}.
     * </p>
     *
     * @param id    the permission identifier
     * @param roles the associated roles
     */
    record DefaultPermission(
        Long id, Set<String> roles
    ) implements Permission {

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public Set<String> getRoles() {
            return roles;
        }
    }
}
