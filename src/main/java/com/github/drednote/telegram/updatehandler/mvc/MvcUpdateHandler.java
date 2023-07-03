package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.updatehandler.response.NotHandledHandlerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;

@RequiredArgsConstructor
public class MvcUpdateHandler implements UpdateHandler {

  private final HandlerMethodPopular handlerMethodPopular;
  private final HandlerMethodInvoker handlerMethodInvoker = new DefaultHandlerMethodInvoker();

  @Override
  public void onUpdate(UpdateRequest request) {
    handlerMethodPopular.populate(request);
    HandlerMethod handlerMethod = request.getHandlerMethod();
    if (handlerMethod != null) {
      Object invoked = handlerMethodInvoker.invoke(request);
      Class<?> parameterType = handlerMethod.getReturnType().getParameterType();
      setResponse(request, invoked, () -> parameterType);
    }
    request.setResponse(new NotHandledHandlerResponse(request.getOrigin()));
  }
}
