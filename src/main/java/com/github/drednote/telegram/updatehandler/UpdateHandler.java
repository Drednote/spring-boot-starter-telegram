package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.response.CompositeHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.EmptyHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.GenericHandlerResponse;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface UpdateHandler {

  void onUpdate(UpdateRequest request) throws Exception;

  /**
   * @param request       base request
   * @param invoked       result of UpdateHandler
   * @param parameterType Supplier because invoked can be null, and parameterType supplier will be
   *                      called after null checked
   */
  @SuppressWarnings("unchecked")
  default void setResponse(
      UpdateRequest request, Object invoked, Supplier<Class<?>> parameterType
  ) {
    if (invoked == null || Void.TYPE.isAssignableFrom(parameterType.get())) {
      request.setResponse(new EmptyHandlerResponse());
    } else if (HandlerResponse.class.isAssignableFrom(parameterType.get())) {
      request.setResponse((HandlerResponse) invoked);
    } else if (Collection.class.isAssignableFrom(parameterType.get())
        && elementsIsHandlerResponses((Collection<?>) invoked)) {
      request.setResponse(new CompositeHandlerResponse((List<HandlerResponse>) invoked));
    } else {
      request.setResponse(new GenericHandlerResponse(invoked));
    }
  }

  private boolean elementsIsHandlerResponses(Collection<?> invoked) {
    for (Object o : invoked) {
      if (!(o instanceof HandlerResponse)) {
        return false;
      }
    }
    return true;
  }

  @SuppressWarnings({"Convert2MethodRef", "java:S1612"})
  default void setResponse(UpdateRequest request, Object invoked) {
    setResponse(request, invoked, () -> invoked.getClass());
  }

}
