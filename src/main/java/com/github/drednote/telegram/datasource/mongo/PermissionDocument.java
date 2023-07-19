package com.github.drednote.telegram.datasource.mongo;

import com.github.drednote.telegram.datasource.Permission;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "permissions")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class PermissionDocument implements Permission {

  @Id
  private Long id;
  private Set<String> roles;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PermissionDocument that)) {
      return false;
    }
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
