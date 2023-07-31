package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class ForbiddenHandlerResponse extends AbstractHandlerResponse {

  public static final ForbiddenHandlerResponse INSTANCE = new ForbiddenHandlerResponse();

  private ForbiddenHandlerResponse() {
    super("response.forbidden", "You do not have access to this bot!");
  }

  @Override
  public void process(UpdateRequest updateRequest) throws TelegramApiException {
    String text = getMessageForLocale(updateRequest);
    sendString(text, updateRequest);
  }
}
