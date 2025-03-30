package io.github.drednote.telegram.datasource.scenarioid.jpa;

import io.github.drednote.telegram.datasource.scenarioid.ScenarioId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = ScenarioIdEntity.TABLE_NAME)
public class ScenarioIdEntity implements ScenarioId {

    public static final String TABLE_NAME = "scenario_id";

    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "scenario_id", nullable = false)
    private String scenarioId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        ScenarioIdEntity that = (ScenarioIdEntity) o;
        return id != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
