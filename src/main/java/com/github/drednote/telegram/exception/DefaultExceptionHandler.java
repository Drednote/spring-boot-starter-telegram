package com.github.drednote.telegram.exception;

import com.github.drednote.telegram.core.DefaultHandlerMethodInvoker;
import com.github.drednote.telegram.core.HandlerMethodInvoker;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.utils.ResponseSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@RequiredArgsConstructor
public class DefaultExceptionHandler implements ExceptionHandler {

  private final ExceptionHandlerResolver exceptionHandlerResolver;
  private final HandlerMethodInvoker handlerMethodInvoker = new DefaultHandlerMethodInvoker();

  @Override
  public void handle(UpdateRequest request, Throwable throwable) throws Exception {
    request.setError(throwable);
    HandlerMethod handlerMethod = exceptionHandlerResolver.resolve(throwable);
    if (handlerMethod != null) {
      Object invoked = handlerMethodInvoker.invoke(request, handlerMethod);
      ResponseSetter.setResponse(request, invoked,
          () -> handlerMethod.getReturnType().getParameterType());
    } else {
      log.error("For UpdateRequest {} error occurred during update handling", request, throwable);
    }
  }
}
