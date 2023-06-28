package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.mvc.DefaultHandlerMethodInvoker;
import com.github.drednote.telegram.updatehandler.mvc.HandlerMethodInvoker;
import com.github.drednote.telegram.updatehandler.mvc.HandlerMethodPopular;
import com.github.drednote.telegram.updatehandler.response.EmptyHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.GenericHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.NotHandledHandlerResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MvcUpdateHandler implements UpdateHandler {

  private final HandlerMethodPopular handlerMethodPopular;
  private final HandlerMethodInvoker handlerMethodInvoker = new DefaultHandlerMethodInvoker();

  @Override
  public void onUpdate(UpdateRequest request) {
    handlerMethodPopular.populate(request);
    if (request.getHandlerMethod() != null) {
      Object invoked = handlerMethodInvoker.invoke(request);
      Class<?> parameterType = request.getHandlerMethod().getReturnType().getParameterType();
      setResponse(request, invoked, parameterType);
    }
    request.setResponse(new NotHandledHandlerResponse(null));
  }

  private static void setResponse(UpdateRequest request, Object invoked, Class<?> parameterType) {
    if (Void.TYPE.isAssignableFrom(parameterType)) {
      request.setResponse(new EmptyHandlerResponse());
    }
    if (invoked == null) {
      request.setResponse(new NotHandledHandlerResponse(null));
    }
    if (HandlerResponse.class.isAssignableFrom(parameterType)) {
      request.setResponse((HandlerResponse) invoked);
    }
    request.setResponse(new GenericHandlerResponse(null, invoked));
  }
}
