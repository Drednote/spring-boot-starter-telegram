package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class SimpleMessageHandlerResponse extends AbstractHandlerResponse {

  protected SimpleMessageHandlerResponse(String code, String defaultMessage) {
    super(code, defaultMessage);
  }

  @Override
  public void process(TelegramUpdateRequest request) throws TelegramApiException {
    String text = getMessageForLocale(request);
    sendString(text, request);
  }
}
