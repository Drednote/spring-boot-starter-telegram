package com.github.drednote.telegram.exception;

import com.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;

public interface ExceptionHandler {

  void handle(ExtendedTelegramUpdateRequest request);
}
