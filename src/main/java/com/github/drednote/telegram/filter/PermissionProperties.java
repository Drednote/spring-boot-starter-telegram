package com.github.drednote.telegram.filter;

import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram-bot.permission")
@Getter
@Setter
public class PermissionProperties {

  public static final String DEFAULT_ROLE = "NONE";

  /**
   * Define who has access to bot
   */
  private Access access = Access.ALL;
  /**
   * If a user has no role, this will be set by default
   */
  private String defaultRole = DEFAULT_ROLE;
  /**
   * The list of roles with privileges
   */
  private Map<String, Role> roles = Map.of();
  /**
   * The map of [userId:role]
   *
   * @deprecated should be removed. Not safety for production
   */
  @Deprecated(forRemoval = true)
  private Map<Long, Set<String>> assignRole = Map.of();

  public enum Access {
    ALL, BY_ROLE
  }

  @Getter
  @Setter
  public static class Role {

    private boolean canRead;
  }
}
