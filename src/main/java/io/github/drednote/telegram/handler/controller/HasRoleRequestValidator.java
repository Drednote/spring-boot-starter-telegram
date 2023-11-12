package io.github.drednote.telegram.handler.controller;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.exception.type.HasRoleValidationException;
import io.github.drednote.telegram.exception.type.RequestValidationException;
import io.github.drednote.telegram.handler.controller.annotation.HasRole;
import io.github.drednote.telegram.handler.controller.annotation.HasRole.StrategyMatching;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import org.springframework.web.method.HandlerMethod;

public class HasRoleRequestValidator implements RequestValidator {

  @Override
  public void validate(UpdateRequest request) throws RequestValidationException {
    RequestHandler requestHandler = request.getRequestHandler();
    if (requestHandler != null) {
      HandlerMethod handlerMethod = requestHandler.handlerMethod();
      HasRole hasRole = handlerMethod.getMethodAnnotation(HasRole.class);
      if (hasRole != null) {
        boolean isValid = isValid(request, hasRole);
        if (!isValid) {
          throw new HasRoleValidationException(
              "The method %s annotated with @HasRole annotation and user has no matching role %s"
                  .formatted(handlerMethod, Arrays.toString(hasRole.value())));
        }
      }
    }
  }

  private boolean isValid(UpdateRequest request, HasRole hasRole) {
    Permission permission = Objects.requireNonNull(request.getPermission());
    Set<String> roles = defaultIfNull(permission.getRoles(), Set.of());
    StrategyMatching strategy = hasRole.strategyMatching();
    if (strategy == StrategyMatching.INTERSECTION) {
      return Arrays.stream(hasRole.value()).anyMatch(roles::contains);
    } else {
      return Arrays.stream(hasRole.value()).allMatch(roles::contains);
    }
  }
}
