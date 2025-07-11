package io.github.drednote.telegram.datasource.session.jpa;

import io.github.drednote.telegram.datasource.session.UpdateInbox;
import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
@Entity
@Table(name = "update_inbox", indexes = {
    @Index(name = "idx_update_inbox_created_at", columnList = "created_at"),
    @Index(name = "idx_update_inbox_entity_id", columnList = "entity_id"),
    @Index(name = "idx_update_inbox_status", columnList = "status"),
})
public class JpaUpdateInbox extends UpdateInbox {

    @Id
    @Column(name = "update_id", nullable = false)
    private Integer updateId;

    @Nullable
    @Column(name = "entity_id")
    private String entityId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "update", nullable = false)
    private Update update;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UpdateInboxStatus status;
    @Column(name = "error_description")
    private String errorDescription;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JpaUpdateInbox that = (JpaUpdateInbox) o;
        return Objects.equals(updateId, that.updateId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(updateId);
    }

    @Override
    public String toString() {
        return "JpaUpdateInbox{" +
               "updateId=" + updateId +
               '}';
    }
}
