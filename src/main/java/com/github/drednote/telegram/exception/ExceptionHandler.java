package com.github.drednote.telegram.exception;

import com.github.drednote.telegram.core.UpdateRequest;

public interface ExceptionHandler {

  void handle(UpdateRequest request) throws Exception;
}
