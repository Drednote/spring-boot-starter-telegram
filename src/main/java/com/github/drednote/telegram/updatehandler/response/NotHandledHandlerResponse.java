package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class NotHandledHandlerResponse extends AbstractHandlerResponse {

  @Override
  public void process(UpdateRequest updateRequest) throws TelegramApiException {
    AbsSender absSender = updateRequest.getAbsSender();
    Long chatId = updateRequest.getChatId();
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText("Неизвестная команда или текст, попробуйте что нибудь другое");
    absSender.execute(sendMessage);
  }
}
