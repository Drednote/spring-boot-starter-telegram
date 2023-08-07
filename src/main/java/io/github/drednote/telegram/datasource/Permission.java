package io.github.drednote.telegram.datasource;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface Permission {

  Set<String> getRoles();

  @Getter
  @AllArgsConstructor
  class DefaultPermission implements Permission {

    private Set<String> roles;
  }
}
