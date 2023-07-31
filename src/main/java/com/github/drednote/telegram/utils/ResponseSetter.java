package com.github.drednote.telegram.utils;

import com.github.drednote.telegram.core.ExtendedBotRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import com.github.drednote.telegram.updatehandler.response.CompositeHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.EmptyHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.GenericHandlerResponse;
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
      ExtendedBotRequest request, Object invoked, Supplier<Class<?>> parameterType
  ) {
    if (invoked == null || Void.TYPE.isAssignableFrom(parameterType.get())) {
      request.setResponse(EmptyHandlerResponse.INSTANCE);
    } else if (HandlerResponse.class.isAssignableFrom(parameterType.get())) {
      request.setResponse((HandlerResponse) invoked);
    } else if (Collection.class.isAssignableFrom(parameterType.get())
        && elementsIsHandlerResponses((Collection<?>) invoked)) {
      request.setResponse(new CompositeHandlerResponse((List<HandlerResponse>) invoked));
    } else {
      request.setResponse(new GenericHandlerResponse(invoked));
    }
  }

  @SuppressWarnings({"Convert2MethodRef", "java:S1612"})
  public void setResponse(ExtendedBotRequest request, Object invoked) {
    setResponse(request, invoked, () -> invoked.getClass());
  }

  private boolean elementsIsHandlerResponses(Collection<?> invoked) {
    for (Object o : invoked) {
      if (!(o instanceof HandlerResponse)) {
        return false;
      }
    }
    return true;
  }
}
