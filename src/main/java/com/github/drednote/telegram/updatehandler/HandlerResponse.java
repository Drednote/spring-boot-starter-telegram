package com.github.drednote.telegram.updatehandler;

import org.springframework.core.Ordered;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface HandlerResponse extends Ordered {

  Update getUpdate();

  /**
   * Метод отправки
   *
   * @param absSender отправитель
   * @throws TelegramApiException если не получилось отправить
   */
  void process(AbsSender absSender) throws TelegramApiException;
}
