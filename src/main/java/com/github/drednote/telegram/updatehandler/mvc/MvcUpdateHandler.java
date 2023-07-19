package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.DefaultHandlerMethodInvoker;
import com.github.drednote.telegram.core.HandlerMethodInvoker;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.utils.ResponseSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class MvcUpdateHandler implements UpdateHandler {

  private final HandlerMethodPopular handlerMethodPopular;
  private final HandlerMethodInvoker handlerMethodInvoker = new DefaultHandlerMethodInvoker();

  @Override
  public void onUpdate(UpdateRequest request) throws Exception {
    handlerMethodPopular.populate(request);
    HandlerMethod handlerMethod = request.getHandlerMethod();
    if (handlerMethod != null) {
      Class<?> parameterType = handlerMethod.getReturnType().getParameterType();
      Object invoked = handlerMethodInvoker.invoke(request, handlerMethod);
      ResponseSetter.setResponse(request, invoked, () -> parameterType);
    }
  }
}
