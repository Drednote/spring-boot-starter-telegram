package com.github.drednote.telegram.updatehandler.mvc;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface HandlerMethodInvoker {

  @Nullable
  Object invoke(HandlerMethod handlerMethod, Update update);
}
