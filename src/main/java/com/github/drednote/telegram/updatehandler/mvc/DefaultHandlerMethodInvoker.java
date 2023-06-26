package com.github.drednote.telegram.updatehandler.mvc;

import java.lang.reflect.InvocationTargetException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class DefaultHandlerMethodInvoker implements HandlerMethodInvoker {

  @Override
  @Nullable
  public Object invoke(HandlerMethod handlerMethod, Update update) {
    try {
      return handlerMethod.getMethod().invoke(handlerMethod.getBean(), update);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("Cannot initiate BotRequest method {}", handlerMethod, e);
      return null;
    }
  }
}
