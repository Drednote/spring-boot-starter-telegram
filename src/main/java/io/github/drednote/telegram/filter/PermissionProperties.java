package io.github.drednote.telegram.filter;

import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for defining access permissions and roles.
 *
 * <p>The properties defined in this class are used for enforcing access permissions and roles
 * in various filters and components.
 *
 * @author Ivan Galushko
 */
@Configuration
@ConfigurationProperties("drednote.telegram.filters.permission")
@Getter
@Setter
public class PermissionProperties {

    /**
     * The default role assigned to users with no roles.
     */
    public static final String DEFAULT_ROLE = "NONE";

    /**
     * Define who has access to bot
     */
    private Access access = Access.ALL;
    /**
     * If a user has no role, this role will be set by default
     */
    private String defaultRole = DEFAULT_ROLE;
    /**
     * The list of roles with privileges
     */
    private Map<String, Role> roles = Map.of();
    /**
     * The map of [userId:role]
     */
    private Map<Long, Set<String>> assignRole = Map.of();

    /**
     * Enumeration of access control modes.
     */
    public enum Access {
        /**
         * All users have access to bot
         */
        ALL,
        /**
         * Only users with specific privilege have access to bot
         */
        BY_ROLE
    }

    /**
     * Defines the privileges and attributes of a role
     */
    @Getter
    @Setter
    public static class Role {

        /**
         * Indicates whether the role has read privileges
         */
        private boolean canRead;

        /**
         * The map of additional permissions.
         */
        private Map<String, Object> permissions = Map.of();
    }
}
