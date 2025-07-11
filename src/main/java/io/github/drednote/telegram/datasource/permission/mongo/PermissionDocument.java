package io.github.drednote.telegram.datasource.permission.mongo;

import io.github.drednote.telegram.datasource.permission.Permission;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "permissions")
@Getter
@Setter
@ToString
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
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
