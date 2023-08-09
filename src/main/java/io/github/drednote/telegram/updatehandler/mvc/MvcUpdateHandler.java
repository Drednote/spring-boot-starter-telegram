package io.github.drednote.telegram.updatehandler.mvc;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.core.invoke.DefaultHandlerMethodInvoker;
import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.updatehandler.UpdateHandler;
import io.github.drednote.telegram.core.ResponseSetter;
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
  public void onUpdate(TelegramUpdateRequest request) throws Exception {
    handlerMethodPopular.populate(request);
    RequestHandler requestHandler = request.getRequestHandler();
    if (requestHandler != null) {
      HandlerMethod handlerMethod = requestHandler.handlerMethod();
      Class<?> parameterType = handlerMethod.getReturnType().getParameterType();
      Object invoked = handlerMethodInvoker.invoke(request, handlerMethod);
      ResponseSetter.setResponse(request, invoked, () -> parameterType);
    }
  }
}
