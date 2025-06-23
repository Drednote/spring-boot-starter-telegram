package io.github.drednote.telegram.exception;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.type.TextReturningException;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import io.github.drednote.telegram.response.InternalErrorTelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * The {@code DefaultExceptionHandler} class implements the {@code ExceptionHandler} interface and
 * serves as a default exception handler for {@code UpdateRequest} processing. This class
 * uses the {@link  ExceptionHandlerResolver} and {@link HandlerMethodInvoker} for resolving and
 * invoking the handler method. It also provides internal methods for handling various types of
 * exceptions
 *
 * @author Ivan Galushko
 * @see ExceptionHandlerResolver
 * @see HandlerMethodInvoker
 */
public class DefaultExceptionHandler implements ExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

  private final ExceptionHandlerResolver exceptionHandlerResolver;
  private final HandlerMethodInvoker handlerMethodInvoker;

  public DefaultExceptionHandler(
      ExceptionHandlerResolver exceptionHandlerResolver, HandlerMethodInvoker handlerMethodInvoker
  ) {
    Assert.required(exceptionHandlerResolver, "ExceptionHandlerResolver");
    Assert.required(handlerMethodInvoker, "HandlerMethodInvoker");

    this.exceptionHandlerResolver = exceptionHandlerResolver;
    this.handlerMethodInvoker = handlerMethodInvoker;
  }

  /**
   * Handles exceptions that occur during the processing of a {@code UpdateRequest}. It
   * resolves the appropriate handler method for the thrown exception and invokes it to handle the
   * request. If no handler method is found, it logs the error and optionally does some stuff
   *
   * @param request the {@code UpdateRequest} object representing the request to be
   *                processed
   */
  @Override
  public void handle(UpdateRequest request) {
    Assert.notNull(request, "UpdateRequest");

    Throwable throwable = request.getError();
    if (throwable == null) {
      return;
    }
    HandlerMethod handlerMethod = exceptionHandlerResolver.resolve(throwable);
    if (handlerMethod != null) {
      try {
        Object invoked = handlerMethodInvoker.invoke(request, handlerMethod);
        ResponseSetter.setResponse(request, invoked,
            handlerMethod.getReturnType().getParameterType());
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
    } else if (throwable instanceof TextReturningException textReturningException) {
      log.warn(textReturningException.getMessage());
      request.getAccessor().setResponse(new GenericTelegramResponse(textReturningException.getMessage()));
    } else {
      if (request.getProperties().getUpdateHandler().isSetDefaultErrorAnswer()
          && request.getResponse() == null) {
        request.getAccessor().setResponse(new InternalErrorTelegramResponse());
      }
      log.error("For UpdateRequest {} error occurred during update handling", request, throwable);
    }
  }
}
