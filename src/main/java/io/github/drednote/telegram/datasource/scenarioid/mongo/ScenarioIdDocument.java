package io.github.drednote.telegram.datasource.scenarioid.mongo;

import io.github.drednote.telegram.datasource.scenarioid.ScenarioId;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Document(collection = ScenarioIdDocument.TABLE_NAME)
public class ScenarioIdDocument implements ScenarioId {

    public static final String TABLE_NAME = "scenario_id";

    @Id
    private String id;
    private String scenarioId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        ScenarioIdDocument that = (ScenarioIdDocument) o;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
