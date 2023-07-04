package com.github.drednote.telegram.exception;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public interface ExceptionHandlerResolver {

  @Nullable
  HandlerMethod resolve(Throwable throwable);
}
