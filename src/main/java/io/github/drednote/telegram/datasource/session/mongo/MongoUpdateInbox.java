package io.github.drednote.telegram.datasource.session.mongo;

import io.github.drednote.telegram.datasource.session.UpdateInbox;
import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
@Document(collection = "update_inbox")
public class MongoUpdateInbox extends UpdateInbox {

    @Id
    private Integer updateId;

    @Nullable
    @Indexed
    private String entityId;
    private Update update;
    @Indexed
    private UpdateInboxStatus status;
    private String errorDescription;

    @CreatedDate
    @Indexed
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MongoUpdateInbox that = (MongoUpdateInbox) o;
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
