package io.github.drednote.telegram.core.resolver;

import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * The {@code HandlerMethodArgumentResolver} interface defines methods for resolving method
 * arguments in a handler method that is marked by annotations for being invoked by
 * {@link HandlerMethodInvoker}. Implementations of this interface provide the logic to resolve
 * specific types of method arguments
 *
 * @author Ivan Galushko
 * @see HandlerMethodInvoker
 */
public interface HandlerMethodArgumentResolver extends Ordered {

  /**
   * The order for parameters marked by an annotation
   */
  int FIRST_ORDER = HIGHEST_PRECEDENCE + 100;
  /**
   * The order for all other types
   */
  int SECOND_ORDER = HIGHEST_PRECEDENCE + 200;
  /**
   * The exception message for unknown parameters
   */
  String UNKNOWN_PARAMETER_EXCEPTION_MESSAGE = "Found unknown parameter %s. "
      + "Consider call supportsParameter method before actually try to resolve parameter";

  /**
   * Resolves the argument value for the given method parameter and update request
   *
   * @param parameter the method parameter to resolve, not null
   * @param request   the update request, not null
   * @return the resolved argument value. Can be null
   * @throws IllegalArgumentException if this resolver doesn't support given method parameter
   */
  @Nullable
  Object resolveArgument(@NonNull MethodParameter parameter,
      @NonNull TelegramUpdateRequest request);

  /**
   * Checks if the resolver supports the given method parameter
   *
   * @param parameter the method parameter to resolve, not null
   * @return true if the resolver supports the parameter, false otherwise
   */
  boolean supportsParameter(@NonNull MethodParameter parameter);
}
