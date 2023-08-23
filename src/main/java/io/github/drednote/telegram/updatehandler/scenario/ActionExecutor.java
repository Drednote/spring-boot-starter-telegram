package io.github.drednote.telegram.updatehandler.scenario;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;

@FunctionalInterface
public interface ActionExecutor {

  Object onAction(TelegramUpdateRequest request) throws Exception;
}
