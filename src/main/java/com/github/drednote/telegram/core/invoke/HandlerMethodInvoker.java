package com.github.drednote.telegram.core.invoke;

import com.github.drednote.telegram.core.BotRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public interface HandlerMethodInvoker {

  @Nullable
  Object invoke(BotRequest request, HandlerMethod handlerMethod) throws Exception;
}
