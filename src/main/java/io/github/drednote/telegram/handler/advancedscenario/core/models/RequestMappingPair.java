package io.github.drednote.telegram.handler.advancedscenario.core.models;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;

public class RequestMappingPair {
    private final TelegramRequest telegramRequest;
    private final UpdateRequestMapping updateRequestMapping;

    public RequestMappingPair(TelegramRequest telegramRequest, UpdateRequestMapping updateRequestMapping) {
        this.telegramRequest = telegramRequest;
        this.updateRequestMapping = updateRequestMapping;
    }

    public TelegramRequest getTelegramRequest() {
        return telegramRequest;
    }

    public UpdateRequestMapping getUpdateRequestMapping() {
        return updateRequestMapping;
    }
}
