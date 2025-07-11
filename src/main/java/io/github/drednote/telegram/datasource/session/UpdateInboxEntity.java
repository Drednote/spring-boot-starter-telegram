package io.github.drednote.telegram.datasource.session;

import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class UpdateInboxEntity {

    public abstract Integer getUpdateId();

    public abstract Update getUpdate();

    @Nullable
    public abstract String getEntityId();

    public abstract UpdateInboxStatus getStatus();

    public abstract String getErrorDescription();

    public abstract void setStatus(UpdateInboxStatus status);

    public abstract void setErrorDescription(String errorDescription);

    public abstract void setUpdateId(Integer updateId);

    public abstract void setUpdate(Update update);

    public abstract void setEntityId(@Nullable String entityId);
}
