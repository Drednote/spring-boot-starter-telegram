package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class SimpleMessageTelegramResponse extends AbstractTelegramResponse {

  protected SimpleMessageTelegramResponse(String code, String defaultMessage) {
    super(code, defaultMessage);
  }

  @Override
  public void process(TelegramUpdateRequest request) throws TelegramApiException {
    String text = getMessageForLocale(request);
    sendString(text, request);
  }
}
