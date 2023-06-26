package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.updatehandler.mvc.DefaultHandlerMethodInvoker;
import com.github.drednote.telegram.updatehandler.mvc.HandlerMethodInvoker;
import com.github.drednote.telegram.updatehandler.mvc.HandlerMethodLookup;
import com.github.drednote.telegram.updatehandler.response.EmptyUpdateHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.GenericUpdateHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.NotHandledUpdateHandlerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class MvcUpdateHandler implements UpdateHandler {

  private final HandlerMethodLookup handlerMethodLookup;
  private final HandlerMethodInvoker handlerMethodInvoker = new DefaultHandlerMethodInvoker();

  @Override
  public UpdateHandlerResponse onUpdate(Update update) {
    HandlerMethod handlerMethod = handlerMethodLookup.lookup(update);
    if (handlerMethod != null) {
      Object invoked = handlerMethodInvoker.invoke(handlerMethod, update);
      Class<?> parameterType = handlerMethod.getReturnType().getParameterType();
      if (Void.TYPE.isAssignableFrom(parameterType)) {
        return new EmptyUpdateHandlerResponse(update);
      }
      if (invoked == null) {
        return new NotHandledUpdateHandlerResponse(update);
      }
      if (UpdateHandlerResponse.class.isAssignableFrom(parameterType)) {
        return ((UpdateHandlerResponse) invoked);
      }
      return new GenericUpdateHandlerResponse(update, invoked);
    }
    return new EmptyUpdateHandlerResponse(update);
  }
}
