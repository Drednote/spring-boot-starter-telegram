package io.github.drednote.telegram.core.request;

import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.handler.controller.RequestHandler;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.response.TelegramResponse;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * The implementation of {@link UpdateRequest} needed only for parsing {@link Update}
 *
 * @author Ivan Galushko
 */
public class ParsedUpdateRequest extends AbstractUpdateRequest {

    @Nullable
    private final TelegramClient telegramClient;

    public ParsedUpdateRequest(Update update, @Nullable TelegramClient telegramClient) {
        super(update);
        this.telegramClient = telegramClient;
    }

    @Override
    @NonNull
    public TelegramClient getTelegramClient() {
        if (telegramClient == null) {
            throw new IllegalStateException("TelegramClient is null");
        }
        return telegramClient;
    }

    @Override
    public Permission getPermission() {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public Scenario<?> getScenario() {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public TelegramResponse getResponse() {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    @NonNull
    public List<Object> getResponseFromTelegram() {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public Exception getError() {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public RequestHandler getRequestHandler() {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public void setScenario(@Nullable Scenario<?> scenario) {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public void setResponse(@Nullable TelegramResponse response) {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public void addResponse(TelegramResponse response) {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public void setRequestHandler(@Nullable RequestHandler requestHandler) {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public void setPermission(@Nullable Permission permission) {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public void addResponseFromTelegram(@Nullable Object response) {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }

    @Override
    public void setError(Throwable error) {
        throw new UnsupportedOperationException("Not supported in this implementation");
    }
}
