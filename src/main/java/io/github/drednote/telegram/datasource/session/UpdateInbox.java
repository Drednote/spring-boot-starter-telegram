package io.github.drednote.telegram.datasource.session;

import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateInbox {

    Integer getUpdateId();

    Update getUpdate();

    @Nullable
    String getEntityId();

    UpdateInboxStatus getStatus();

    void setStatus(UpdateInboxStatus status);

    void setErrorDescription(String errorDescription);
}
