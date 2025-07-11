package io.github.drednote.telegram.datasource.session.inmemory;

import io.github.drednote.telegram.datasource.session.UpdateInbox;
import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
public class InMemoryUpdateInbox extends UpdateInbox {

    private Integer updateId;

    @Nullable
    private String entityId;
    private Update update;
    private UpdateInboxStatus status;
    private String errorDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InMemoryUpdateInbox that = (InMemoryUpdateInbox) o;
        return Objects.equals(updateId, that.updateId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(updateId);
    }

    @Override
    public String toString() {
        return "InMemoryUpdateInbox{" +
               "updateId=" + updateId +
               '}';
    }
}