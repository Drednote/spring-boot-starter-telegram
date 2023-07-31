package com.github.drednote.telegram.exception;

import com.github.drednote.telegram.core.ExtendedBotRequest;

public interface ExceptionHandler {

  void handle(ExtendedBotRequest request);
}
