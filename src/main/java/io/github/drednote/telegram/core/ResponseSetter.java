package io.github.drednote.telegram.core;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.EmptyTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
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
  public static void setResponse(
      @NonNull UpdateRequest request, @Nullable Object invoked,
      @Nullable Class<?> parameterType
  ) {
    Assert.notNull(request, "UpdateRequest");
    if (invoked == null || parameterType == null || Void.TYPE.isAssignableFrom(parameterType)) {
      request.getAccessor().setResponse(EmptyTelegramResponse.INSTANCE);
    } else if (TelegramResponse.class.isAssignableFrom(parameterType)) {
      request.getAccessor().setResponse((TelegramResponse) invoked);
    } else if (Collection.class.isAssignableFrom(parameterType)) {
      request.getAccessor().setResponse(new CompositeTelegramResponse(convertIfNeeded(((Collection<?>) invoked))));
    } else {
      request.getAccessor().setResponse(new GenericTelegramResponse(invoked));
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
   *
   * @param invoked the collection
   */
  public static Collection<TelegramResponse> convertIfNeeded(Collection<?> invoked) {
    Collection<TelegramResponse> responses = new ArrayList<>();
    for (Object o : invoked) {
      if (o instanceof TelegramResponse telegramResponse) {
        responses.add(telegramResponse);
      } else {
        responses.add(new GenericTelegramResponse(o));
      }
    }
    return responses;
  }
}
