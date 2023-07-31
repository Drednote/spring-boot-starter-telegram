package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.ExtendedBotRequest;
import com.github.drednote.telegram.core.invoke.DefaultHandlerMethodInvoker;
import com.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import com.github.drednote.telegram.utils.ResponseSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 200)
public class MvcUpdateHandler implements UpdateHandler {

  private final HandlerMethodPopular handlerMethodPopular;
  private final HandlerMethodInvoker handlerMethodInvoker = new DefaultHandlerMethodInvoker();

  @Override
  public void onUpdate(ExtendedBotRequest request) throws Exception {
    handlerMethodPopular.populate(request);
    HandlerMethod handlerMethod = request.getRequestHandler().handlerMethod();
    if (handlerMethod != null) {
      Class<?> parameterType = handlerMethod.getReturnType().getParameterType();
      Object invoked = handlerMethodInvoker.invoke(request, handlerMethod);
      ResponseSetter.setResponse(request, invoked, () -> parameterType);
    }
  }
}
