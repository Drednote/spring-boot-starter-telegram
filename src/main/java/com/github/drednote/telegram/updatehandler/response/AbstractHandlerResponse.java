package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbstractHandlerResponse implements HandlerResponse {

  protected void sendString(
      String string, UpdateRequest updateRequest
  ) throws TelegramApiException {
    AbsSender absSender = updateRequest.getAbsSender();
    Long chatId = updateRequest.getChatId();
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText(string);
    absSender.execute(sendMessage);
  }
}
