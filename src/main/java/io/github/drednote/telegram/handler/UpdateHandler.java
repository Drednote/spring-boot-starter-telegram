package io.github.drednote.telegram.handler;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.ExceptionHandler;
import io.github.drednote.telegram.handler.controller.ControllerUpdateHandler;
import io.github.drednote.telegram.handler.scenario.ScenarioUpdateHandler;
import io.github.drednote.telegram.response.TelegramResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * This interface defines the contract for handling Telegram {@link Update}s. Implementations of
 * this interface are responsible for processing incoming updates and performing appropriate actions
 * based on the implementation and on the content of the {@code update}.
 * <p>
 * For handling updates can be written many {@code UpdateHandler}s and to specify priority of
 * execution you can use {@link Order} annotation.
 * <p>
 * Also, after successful processing of the message, it is necessary set {@link TelegramResponse} in
 * {@link UpdateRequest}, so that update processing can be considered successful. If this is
 * not done, further update handlers will be called
 * <p>
 * You can make your own handlers by implementing this interface and mark them with
 * {@link Component}. They will be picked automatically
 * <p>
 * You can disable already existing handlers by setting necessary properties in
 * {@link UpdateHandlerProperties} to {@code false}
 *
 * @author Ivan Galushko
 * @see UpdateRequest
 * @see UpdateHandlerProperties
 * @see ControllerUpdateHandler
 * @see ScenarioUpdateHandler
 */
public interface UpdateHandler {

  /**
   * Handles a Telegram update request by performing the required actions based on the
   * implementation and on the content of the {@code update}.
   * <p>
   * By default, all errors will be caught with {@link ExceptionHandler}
   *
   * @param request The Telegram update request to be handled, not null
   * @throws Exception if an error occurs while processing the update
   */
  void onUpdate(@NonNull UpdateRequest request) throws Exception;

}
