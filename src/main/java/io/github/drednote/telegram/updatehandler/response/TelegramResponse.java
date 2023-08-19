package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FunctionalInterface
public interface TelegramResponse {

  /**
   * Sending method
   *
   * @throws TelegramApiException if sending failed
   */
  void process(TelegramUpdateRequest request) throws TelegramApiException;
}
