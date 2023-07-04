package com.github.drednote.telegram.core;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public interface HandlerMethodInvoker {

  @Nullable
  Object invoke(UpdateRequest updateRequest, HandlerMethod handlerMethod) throws Exception;
}
