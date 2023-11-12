package io.github.drednote.telegram.handler.controller;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.type.RequestValidationException;
import io.github.drednote.telegram.utils.Assert;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeRequestValidator implements RequestValidator {

  private final List<RequestValidator> validators;

  public CompositeRequestValidator(List<RequestValidator> validators) {
    Assert.required(validators, "RequestValidators");

    this.validators = validators.stream()
        .filter(it -> !(it instanceof CompositeRequestValidator))
        .collect(Collectors.toList());
    this.validators.add(new HasRoleRequestValidator());
  }

  @Override
  public void validate(UpdateRequest request) throws RequestValidationException {
    for (RequestValidator validator : validators) {
      validator.validate(request);
    }
  }
}
