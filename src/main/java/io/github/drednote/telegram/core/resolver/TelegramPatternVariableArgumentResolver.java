package io.github.drednote.telegram.core.resolver;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.mvc.RequestHandler;
import io.github.drednote.telegram.updatehandler.mvc.annotation.TelegramPatternVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;

public class TelegramPatternVariableArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public Object resolveArgument(
      @NonNull MethodParameter parameter, @NonNull TelegramUpdateRequest request
  ) {
    TelegramPatternVariable pathVariable = AnnotatedElementUtils
        .findMergedAnnotation(parameter.getParameter(), TelegramPatternVariable.class);
    if (pathVariable == null) {
      throw new IllegalArgumentException(
          "No PathVariable annotation found on parameter %s".formatted(parameter));
    }
    if (Map.class.isAssignableFrom(parameter.getParameterType())) {
      return Optional.ofNullable(request.getRequestHandler())
          .map(RequestHandler::templateVariables)
          .map(HashMap::new)
          .orElseGet(HashMap::new);
    }
    String name =
        pathVariable.name().isEmpty() ? parameter.getParameter().getName() : pathVariable.name();
    String result = Optional.ofNullable(request.getRequestHandler())
        .map(RequestHandler::templateVariables)
        .map(map -> map.get(name))
        .orElse(null);
    if (result == null) {
      if (pathVariable.required()) {
        throw new IllegalStateException(
            """
                Parameter %s marked as required, but no value present in request.
                If null value is valid - set @PathVariable#requried - false
                """.formatted(parameter));
      }
      if (parameter.isOptional()) {
        return Optional.empty();
      }
    }
    return result;
  }

  @Override
  public boolean supportsParameter(@NonNull MethodParameter parameter) {
    return AnnotatedElementUtils.hasAnnotation(parameter.getParameter(),
        TelegramPatternVariable.class);
  }

  @Override
  public int getOrder() {
    return FIRST_ORDER;
  }
}
