package io.github.drednote.telegram.core;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.response.TelegramResponse;
import io.github.drednote.telegram.updatehandler.response.CompositeTelegramResponse;
import io.github.drednote.telegram.updatehandler.response.EmptyTelegramResponse;
import io.github.drednote.telegram.updatehandler.response.GenericTelegramResponse;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResponseSetter {

  /**
   * @param request       base request
   * @param invoked       result of UpdateHandler
   * @param parameterType Supplier because invoked can be null, and parameterType supplier will be
   *                      called after null checked
   */
  @SuppressWarnings("unchecked")
  public void setResponse(
      TelegramUpdateRequest request, Object invoked, Supplier<Class<?>> parameterType
  ) {
    if (invoked == null || Void.TYPE.isAssignableFrom(parameterType.get())) {
      request.setResponse(EmptyTelegramResponse.INSTANCE);
    } else if (TelegramResponse.class.isAssignableFrom(parameterType.get())) {
      request.setResponse((TelegramResponse) invoked);
    } else if (Collection.class.isAssignableFrom(parameterType.get())
        && elementsIsHandlerResponses((Collection<?>) invoked)) {
      request.setResponse(new CompositeTelegramResponse((List<TelegramResponse>) invoked));
    } else {
      request.setResponse(new GenericTelegramResponse(invoked));
    }
  }

  @SuppressWarnings({"Convert2MethodRef", "java:S1612"})
  public void setResponse(TelegramUpdateRequest request, Object invoked) {
    setResponse(request, invoked, () -> invoked.getClass());
  }

  private boolean elementsIsHandlerResponses(Collection<?> invoked) {
    for (Object o : invoked) {
      if (!(o instanceof TelegramResponse)) {
        return false;
      }
    }
    return true;
  }
}
