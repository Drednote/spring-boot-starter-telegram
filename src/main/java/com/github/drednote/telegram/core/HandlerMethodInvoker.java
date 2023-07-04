package com.github.drednote.telegram.core;

import org.springframework.lang.Nullable;

public interface HandlerMethodInvoker {

  @Nullable
  Object invoke(UpdateRequest mvcUpdateRequest) throws Exception;
}
