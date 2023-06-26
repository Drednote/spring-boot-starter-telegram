package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.updatehandler.UpdateHandlerResponse;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class EmptyUpdateHandlerResponse implements UpdateHandlerResponse {

  private final Update update;

  @Override
  public Update getUpdate() {
    return update;
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
