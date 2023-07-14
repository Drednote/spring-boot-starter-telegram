package com.github.drednote.telegram.exception;

import com.github.drednote.telegram.core.DefaultHandlerMethodInvoker;
import com.github.drednote.telegram.core.HandlerMethodInvoker;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.response.InternalErrorHandlerResponse;
import com.github.drednote.telegram.utils.ResponseSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor
public class DefaultExceptionHandler implements ExceptionHandler {

  private final ExceptionHandlerResolver exceptionHandlerResolver;
  private final HandlerMethodInvoker handlerMethodInvoker = new DefaultHandlerMethodInvoker();

  @Override
  public void handle(UpdateRequest request) throws Exception {
    Throwable throwable = request.getError();
    HandlerMethod handlerMethod = exceptionHandlerResolver.resolve(throwable);
    if (handlerMethod != null) {
      Object invoked = handlerMethodInvoker.invoke(request, handlerMethod);
      ResponseSetter.setResponse(request, invoked,
          () -> handlerMethod.getReturnType().getParameterType());
    } else {
      processNotHandled(throwable, request);
    }
  }

  private void processNotHandled(Throwable throwable, UpdateRequest request) {
    if (throwable instanceof TelegramApiException telegramApiException) {
      log.error("Cannot send response for request '{}' to telegram, cause: ", request.getId(),
          telegramApiException);
    } else {
      if (request.getProperties().getUpdateHandler().isSetDefaultErrorAnswer()) {
        request.setResponse(new InternalErrorHandlerResponse());
      }
      log.error("For UpdateRequest {} error occurred during update handling", request, throwable);
    }
  }
}
