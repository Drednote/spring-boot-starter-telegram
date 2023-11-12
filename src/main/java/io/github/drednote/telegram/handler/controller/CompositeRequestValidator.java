package io.github.drednote.telegram.handler.controller;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.type.RequestValidationException;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.List;

public class CompositeRequestValidator implements RequestValidator {

  private final List<RequestValidator> validators;

  public CompositeRequestValidator(List<RequestValidator> validators) {
    Assert.required(validators, "RequestValidators");

    this.validators = new ArrayList<>(validators);
    this.validators.add(new HasRoleRequestValidator());
  }

  @Override
  public void validate(UpdateRequest request) throws RequestValidationException {
    for (RequestValidator validator : validators) {
      validator.validate(request);
    }
  }
}
