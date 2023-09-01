package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.request.UpdateRequest;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * This functional interface represents a Telegram response action that can be performed. It defines
 * a method for processing a Telegram response using the provided update request.
 * <p>
 * After the update processing is complete, it is expected that a response will be sent to the user.
 * To handle this, you can use this class. Here some useful info about it:
 * <ul>
 *   <li>
 *     <b>Response can only be sent if {@link Update} has a {@code chatId}</b>. So if in {@code Update}
 *     there is no {@code chatId} than you should not use {@code TelegramResponse}
 *     or use {@link EmptyTelegramResponse}
 *   </li>
 *   <li>
 *     Any response from user defined code will automatically be wrapped in the {@code TelegramResponse}
 *     and execute sending method. Rules of wrapping you can see in {@link ResponseSetter}
 *     or in {@link GenericTelegramResponse}
 *   </li>
 *   <li>
 *     There are several utility responses that extend from {@link SimpleMessageTelegramResponse}.
 *     They are used to send simple messages to the current chat that is associated
 *     with {@link UpdateRequest}
 *   </li>
 *   <li>
 *     You can put {@link Order} annotation on impl of {@code TelegramResponse} to specify
 *     the priority of execution. This is useful when a list of {@code TelegramResponse}
 *     is used as an answer.
 *   </li>
 *   <li>
 *     You can create any implementation of `TelegramResponse` for sending response.
 *   </li>
 * </ul>
 *
 * @author Ivan Galushko
 * @implNote Any custom code can be written in {@code TelegramResponse}, but I strongly recommend
 * using this interface only for sending a response to {@code Telegram}
 * @see ResponseSetter
 * @see GenericTelegramResponse
 * @see SimpleMessageTelegramResponse
 */
@FunctionalInterface
public interface TelegramResponse {

  /**
   * Performs the Telegram response action. The {@code UpdateRequest} here for providing info for
   * sending response to Telegram API
   *
   * @param request The update request, not null
   * @throws TelegramApiException if the response processing fails
   */
  void process(@NonNull UpdateRequest request) throws TelegramApiException;
}
