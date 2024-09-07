package io.github.drednote.telegram.datasource.scenario.jpa;

import io.github.drednote.telegram.datasource.scenario.ScenarioEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Table(name = "scenario")
@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class JpaScenarioEntity extends ScenarioEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "state", nullable = false)
    private String state;
    @Column(name = "context", nullable = false)
    private byte[] context;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JpaScenarioEntity that = (JpaScenarioEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
