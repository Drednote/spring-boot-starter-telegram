package io.github.drednote.telegram.core;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.EmptyTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import java.util.Collection;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * The {@code ResponseSetter} class provides static methods for setting the response of a Telegram
 * update request based on the invoked result. It includes methods for setting different types of
 * responses, including empty responses, single responses, composite responses, and generic
 * responses
 *
 * @author Ivan Galushko
 */
public abstract class ResponseSetter {

  private ResponseSetter() {
  }

  /**
   * Sets the response of a Telegram update request based on the invoked result and the parameter
   * type. If the invoked result is null or the parameter type is Void, an empty response is set. If
   * the parameter type is a subclass of {@link TelegramResponse}, the invoked result is set as the
   * response. If the parameter type is a collection and all elements are instances of
   * {@code TelegramResponse}, a composite response is set. Otherwise, a generic response is set
   *
   * @param request       the base request, not null
   * @param invoked       the result of the UpdateHandler, nullable
   * @param parameterType the class of 'invoked' parameter, nullable
   * @see EmptyTelegramResponse
   * @see GenericTelegramResponse
   * @see CompositeTelegramResponse
   */
  @SuppressWarnings("unchecked")
  public static void setResponse(
      @NonNull UpdateRequest request, @Nullable Object invoked,
      @Nullable Class<?> parameterType
  ) {
    if (invoked == null || parameterType == null || Void.TYPE.isAssignableFrom(parameterType)) {
      request.setResponse(EmptyTelegramResponse.INSTANCE);
    } else if (TelegramResponse.class.isAssignableFrom(parameterType)) {
      request.setResponse((TelegramResponse) invoked);
    } else if (Collection.class.isAssignableFrom(parameterType)
        && elementsIsHandlerResponses((Collection<?>) invoked)) {
      request.setResponse(new CompositeTelegramResponse((List<TelegramResponse>) invoked));
    } else {
      request.setResponse(new GenericTelegramResponse(invoked));
    }
  }

  /**
   * Sets the response of a Telegram update request based on the invoked result. Uses the invoked
   * result's class as the parameter type
   *
   * @param request the base request, not null
   * @param invoked the result of the UpdateHandler, maybe null
   */
  public static void setResponse(UpdateRequest request, @Nullable Object invoked) {
    setResponse(request, invoked, invoked != null ? invoked.getClass() : null);
  }

  /**
   * Checks if all elements in a collection are instances of {@link TelegramResponse}
   *
   * @param invoked the collection to check
   * @return true if all elements are instances of {@code TelegramResponse}, false otherwise
   */
  private static boolean elementsIsHandlerResponses(Collection<?> invoked) {
    for (Object o : invoked) {
      if (!(o instanceof TelegramResponse)) {
        return false;
      }
    }
    return true;
  }
}
