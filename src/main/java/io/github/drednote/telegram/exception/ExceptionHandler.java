package io.github.drednote.telegram.exception;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;

public interface ExceptionHandler {

  void handle(TelegramUpdateRequest request);
}
