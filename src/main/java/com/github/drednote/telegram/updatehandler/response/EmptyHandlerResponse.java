package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.updatehandler.HandlerResponse;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class EmptyHandlerResponse implements HandlerResponse {

  @Override
  public Update getUpdate() {
    return null;
  }

  @Override
  public void process(AbsSender absSender) throws TelegramApiException {
    // do nothing
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
