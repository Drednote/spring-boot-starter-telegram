package io.github.drednote.telegram.handler.controller;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.type.RequestValidationException;

public interface RequestValidator {

  void validate(UpdateRequest request) throws RequestValidationException;
}
