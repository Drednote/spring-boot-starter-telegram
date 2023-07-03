package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.updatehandler.HandlerResponse;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class NotHandledHandlerResponse implements HandlerResponse {

  private final Update update;

  @Override
  public Update getUpdate() {
    return update;
  }

  @Override
  public void process(AbsSender absSender) throws TelegramApiException {
    Long chatId = update.getMessage().getChatId();
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText("Неизвестная команда или текст, попробуйте что нибудь другое");
    absSender.execute(sendMessage);
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
