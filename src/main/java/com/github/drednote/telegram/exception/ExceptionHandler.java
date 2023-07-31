package com.github.drednote.telegram.exception;

import com.github.drednote.telegram.core.request.ExtendedBotRequest;

public interface ExceptionHandler {

  void handle(ExtendedBotRequest request);
}
