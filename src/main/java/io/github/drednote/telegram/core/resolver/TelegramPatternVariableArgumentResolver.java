package io.github.drednote.telegram.core.resolver;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.mvc.RequestHandler;
import io.github.drednote.telegram.updatehandler.mvc.annotation.TelegramPatternVariable;
import io.github.drednote.telegram.utils.Assert;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;

/**
 * The {@code TelegramPatternVariableArgumentResolver} class is an implementation of the
 * {@code HandlerMethodArgumentResolver} interface that resolves method arguments annotated with
 * {@link TelegramPatternVariable}. It provides logic to resolve method arguments based on the
 * pattern variables extracted from the request.
 *
 * @author Galushko Ivan
 * @see TelegramPatternVariable
 */
public class TelegramPatternVariableArgumentResolver implements HandlerMethodArgumentResolver {

  /**
   * Resolves the argument value for the given method parameter and update request. Method parameter
   * must be marked with {@link TelegramPatternVariable}
   * <p>
   * Method parameter can be {@link String}, {@link Optional<String>} or
   * {@link java.util.Map Map&lt;String, String&gt;}
   * <p>
   * If the method parameter is {@link java.util.Map Map&lt;String, String&gt;} then the map is
   * populated with all pattern variable names and values
   *
   * @param parameter the method parameter to resolve, not null
   * @param request   the update request, not null
   * @return the resolved argument value. Can be null
   * @throws IllegalArgumentException if the given method parameter isn't marked with
   *                                  {@link TelegramPatternVariable}
   * @see TelegramPatternVariable
   */
  @Override
  @Nullable
  public Object resolveArgument(MethodParameter parameter, TelegramUpdateRequest request) {
    Assert.notNull(parameter, "MethodParameter");
    Assert.notNull(request, "TelegramUpdateRequest");

    TelegramPatternVariable pathVariable = AnnotatedElementUtils
        .findMergedAnnotation(parameter.getParameter(), TelegramPatternVariable.class);
    if (pathVariable == null) {
      throw new IllegalArgumentException(
          "No TelegramPatternVariable annotation found on parameter %s".formatted(parameter));
    }
    if (Map.class.isAssignableFrom(parameter.getParameterType())) {
      return Optional.ofNullable(request.getRequestHandler())
          .map(RequestHandler::templateVariables)
          .map(HashMap::new)
          .orElseGet(HashMap::new);
    }
    return doResolve(parameter, request, pathVariable);
  }

  @Nullable
  private Object doResolve(
      MethodParameter parameter, TelegramUpdateRequest request,
      TelegramPatternVariable variable
  ) {
    String name = variable.name().isEmpty() ? parameter.getParameter().getName() : variable.name();
    String result = Optional.ofNullable(request.getRequestHandler())
        .map(RequestHandler::templateVariables)
        .map(map -> map.get(name))
        .orElse(null);
    if (result == null && variable.required()) {
      throw new IllegalStateException(
          """
              Parameter %s marked as required, but no value present in request.
              If null value is valid - set @TelegramPatternVariable#requried to false
              """.formatted(parameter));
    }
    return parameter.isOptional() ? Optional.ofNullable(result) : result;
  }

  /**
   * Checks if the given method parameter is marked with {@link TelegramPatternVariable}
   *
   * @param parameter the method parameter to check, not null
   * @return true if the given method parameter is marked with {@link TelegramPatternVariable},
   * false otherwise
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Assert.notNull(parameter, "MethodParameter");

    return AnnotatedElementUtils.hasAnnotation(parameter.getParameter(),
        TelegramPatternVariable.class);
  }

  @Override
  public int getOrder() {
    return FIRST_ORDER;
  }
}
