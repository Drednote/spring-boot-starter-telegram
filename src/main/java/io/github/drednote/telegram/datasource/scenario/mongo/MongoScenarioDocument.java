package io.github.drednote.telegram.datasource.scenario.mongo;

import io.github.drednote.telegram.datasource.scenario.ScenarioEntity;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "scenario")
@Getter
@Setter
public class MongoScenarioDocument extends ScenarioEntity {

    @Id
    private String id;
    private String state;
    private byte[] context;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MongoScenarioDocument that = (MongoScenarioDocument) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
