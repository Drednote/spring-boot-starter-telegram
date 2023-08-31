package io.github.drednote.telegram.exception;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

/**
 * The {@code ExceptionHandlerResolver} interface defines a contract for resolving an appropriate
 * {@link HandlerMethod} for a given throwable.
 *
 * <p>Implementing classes are responsible for mapping exceptions to their corresponding handler
 * methods in order to provide effective exception handling within the system
 *
 * @author Ivan Galushko
 */
public interface ExceptionHandlerResolver {

  /**
   * Resolves the appropriate {@link HandlerMethod} for the given throwable
   *
   * @param throwable The throwable for which to resolve the handler, not null
   * @return The resolved handler method, or {@code null} if not found
   */
  @Nullable
  HandlerMethod resolve(@NonNull Throwable throwable);
}
