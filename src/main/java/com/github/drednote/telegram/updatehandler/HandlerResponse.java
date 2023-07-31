package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.core.UpdateRequest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FunctionalInterface
public interface HandlerResponse {

  /**
   * Sending method
   *
   * @throws TelegramApiException if sending failed
   */
  void process(UpdateRequest updateRequest) throws TelegramApiException;
}