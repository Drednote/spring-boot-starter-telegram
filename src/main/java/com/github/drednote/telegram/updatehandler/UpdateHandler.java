package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.response.EmptyHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.GenericHandlerResponse;
import java.util.function.Supplier;

public interface UpdateHandler {

  void onUpdate(UpdateRequest request);

  /**
   * @param request       base request
   * @param invoked       result of UpdateHandler
   * @param parameterType Supplier because invoked can be null, and parameterType supplier will be
   *                      called after null checked
   */
  default void setResponse(
      UpdateRequest request, Object invoked, Supplier<Class<?>> parameterType
  ) {
    if (invoked == null || Void.TYPE.isAssignableFrom(parameterType.get())) {
      request.setResponse(new EmptyHandlerResponse());
    } else if (HandlerResponse.class.isAssignableFrom(parameterType.get())) {
      request.setResponse((HandlerResponse) invoked);
    } else {
      request.setResponse(new GenericHandlerResponse(request.getOrigin(), invoked));
    }
  }

  @SuppressWarnings({"Convert2MethodRef", "java:S1612"})
  default void setResponse(UpdateRequest request, Object invoked) {
    setResponse(request, invoked, () -> invoked.getClass());
  }

}
