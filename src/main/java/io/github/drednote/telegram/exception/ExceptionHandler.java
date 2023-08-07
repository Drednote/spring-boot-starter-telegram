package io.github.drednote.telegram.exception;

import io.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;

public interface ExceptionHandler {

  void handle(ExtendedTelegramUpdateRequest request);
}
