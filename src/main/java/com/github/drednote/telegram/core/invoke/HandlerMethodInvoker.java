package com.github.drednote.telegram.core.invoke;

import com.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public interface HandlerMethodInvoker {

  @Nullable
  Object invoke(TelegramUpdateRequest request, HandlerMethod handlerMethod) throws Exception;
}
