package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Functional interface for defining a Telegram response action.
 */
@FunctionalInterface
public interface TelegramResponse {

  /**
   * Performs the Telegram response action. The {@code TelegramUpdateRequest} here for providing
   * info for sending response to Telegram API
   *
   * @param request The update request
   * @throws TelegramApiException if the response processing fails
   */
  void process(TelegramUpdateRequest request) throws TelegramApiException;
}
