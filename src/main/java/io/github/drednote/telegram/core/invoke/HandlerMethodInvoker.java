package io.github.drednote.telegram.core.invoke;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

/**
 * The {@code HandlerMethodInvoker} interface defines a method for invoking a handler method with
 * the given request and handler method. Implementations of this interface provide the logic to
 * resolve method arguments and invoke the handler method
 *
 * @author Galushko Ivan
 * @see HandlerMethod
 * @see InvocableHandlerMethod
 * @see TelegramUpdateRequest
 */
public interface HandlerMethodInvoker {

  /**
   * Invokes the given handler method with the provided request
   *
   * @param request       the update request, not null
   * @param handlerMethod the handler method to invoke, not null
   * @param providedArgs  additional provided arguments
   * @return the result of invoking the handler method, or null if no result is returned
   * @throws Exception if an error occurs during invocation
   */
  @Nullable
  Object invoke(@NonNull TelegramUpdateRequest request, @NonNull HandlerMethod handlerMethod,
      Object... providedArgs) throws Exception;
}
