package io.github.drednote.telegram.exception;

import io.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;
import io.github.drednote.telegram.core.invoke.DefaultHandlerMethodInvoker;
import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.updatehandler.response.InternalErrorTelegramResponse;
import io.github.drednote.telegram.updatehandler.scenario.ScenarioException;
import io.github.drednote.telegram.core.ResponseSetter;
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
  public void handle(ExtendedTelegramUpdateRequest request) {
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

  private void processInternal(Throwable throwable, ExtendedTelegramUpdateRequest request) {
    if (throwable instanceof TelegramApiException telegramApiException) {
      log.error("Cannot send response {} for request '{}' to telegram, cause: ",
          request.getResponse(), request.getId(), telegramApiException);
    } else if (throwable instanceof ScenarioException scenarioException) {
      // do something
      log.error("For UpdateRequest {} error occurred during update handling", request, scenarioException);
    } else {
      if (request.getProperties().getUpdateHandler().isSetDefaultErrorAnswer()
          && request.getResponse() == null) {
        request.setResponse(InternalErrorTelegramResponse.INSTANCE);
      }
      log.error("For UpdateRequest {} error occurred during update handling", request, throwable);
    }
  }
}
