package io.github.drednote.telegram.datasource.permission;

import java.util.Set;
import org.springframework.lang.Nullable;

public interface Permission {

  Long getId();

  @Nullable
  Set<String> getRoles();

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
