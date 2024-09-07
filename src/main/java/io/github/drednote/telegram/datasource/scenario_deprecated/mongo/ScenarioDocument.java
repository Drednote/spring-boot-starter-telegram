package io.github.drednote.telegram.datasource.scenario_deprecated.mongo;

import io.github.drednote.telegram.datasource.scenario_deprecated.PersistScenario;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collation = "scenarios")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ScenarioDocument implements PersistScenario {

  @Id
  private Long id;
  private String name;
  private String stepName;
  private byte[] context;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ScenarioDocument that)) {
      return false;
    }
    return id != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
