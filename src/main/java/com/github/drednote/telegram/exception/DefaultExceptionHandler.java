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
  public void handle(UpdateRequest request) {
    Throwable throwable = request.getError();
    HandlerMethod handlerMethod = exceptionHandlerResolver.resolve(throwable);
    if (handlerMethod != null) {
      try {
        Object invoked = handlerMethodInvoker.invoke(request, handlerMethod);
        ResponseSetter.setResponse(request, invoked,
            () -> handlerMethod.getReturnType().getParameterType());
      } catch (Exception e) {
        processInternal(e, request);
      }
    } else {
      processInternal(throwable, request);
    }
  }

  private void processInternal(Throwable throwable, UpdateRequest request) {
    if (throwable instanceof TelegramApiException telegramApiException) {
      log.error("Cannot send response {} for request '{}' to telegram, cause: ",
          request.getResponse(), request.getId(), telegramApiException);
    } else {
      if (request.getProperties().getUpdateHandler().isSetDefaultErrorAnswer()
          && request.getResponse() == null) {
        request.setResponse(new InternalErrorHandlerResponse());
      }
      log.error("For UpdateRequest {} error occurred during update handling", request, throwable);
    }
  }
}
