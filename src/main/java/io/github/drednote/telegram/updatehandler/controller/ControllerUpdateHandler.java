package io.github.drednote.telegram.updatehandler.controller;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.UpdateHandler;
import io.github.drednote.telegram.utils.Assert;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;

/**
 * Implementation of the {@code UpdateHandler} interface that handles Telegram updates using the
 * controllers approach. This handler is responsible for invoking appropriate controller methods
 * based on the incoming Telegram update request.
 * <p>
 * Controller classes should be marked with {@link TelegramController} annotation and the methods of
 * this class should be marked with {@link TelegramRequest} or any inherit annotation
 * <p>
 * It uses a {@link HandlerMethodPopular} to populate the necessary information from the incoming
 * request, and a {@link HandlerMethodInvoker} to invoke the corresponding controller method. The
 * response from the controller method is set using a {@link ResponseSetter}.
 * <p>
 * The order of execution for this handler can be specified using the {@link Order} annotation. By
 * default, it is set to {@link Ordered#HIGHEST_PRECEDENCE + 200}.
 *
 * @author Ivan Galushko
 * @see HandlerMethodPopular
 * @see HandlerMethodInvoker
 * @see ResponseSetter
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 200)
public class ControllerUpdateHandler implements UpdateHandler {

  private final HandlerMethodPopular handlerMethodPopular;
  private final HandlerMethodInvoker handlerMethodInvoker;

  /**
   * Constructs an instance of {@code ControllerUpdateHandler}.
   *
   * @param handlerMethodPopular The component responsible for populating the necessary information
   *                             from the incoming update request, not null
   * @param handlerMethodInvoker The component responsible for invoking the controller method, not
   *                             null
   */
  public ControllerUpdateHandler(
      HandlerMethodPopular handlerMethodPopular, HandlerMethodInvoker handlerMethodInvoker
  ) {
    Assert.required(handlerMethodPopular, "HandlerMethodPopular");
    Assert.required(handlerMethodInvoker, "HandlerMethodInvoker");

    this.handlerMethodPopular = handlerMethodPopular;
    this.handlerMethodInvoker = handlerMethodInvoker;
  }

  /**
   * Handles the incoming Telegram update by invoking the appropriate controller method based on the
   * update request. The response from the controller method is set in the request using a {@link
   * ResponseSetter}.
   *
   * @param request The Telegram update request, not null
   * @throws Exception if an error occurs during handling the update
   */
  @Override
  public void onUpdate(TelegramUpdateRequest request) throws Exception {
    handlerMethodPopular.populate(request);
    RequestHandler requestHandler = request.getRequestHandler();
    if (requestHandler != null) {
      HandlerMethod handlerMethod = requestHandler.handlerMethod();
      Class<?> parameterType = handlerMethod.getReturnType().getParameterType();
      Object invoked = handlerMethodInvoker.invoke(request, handlerMethod);
      ResponseSetter.setResponse(request, invoked, parameterType);
    }
  }
}
