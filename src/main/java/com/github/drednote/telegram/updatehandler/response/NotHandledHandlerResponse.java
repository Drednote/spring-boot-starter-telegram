package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class NotHandledHandlerResponse extends AbstractHandlerResponse {

  public static final NotHandledHandlerResponse INSTANCE = new NotHandledHandlerResponse();

  private NotHandledHandlerResponse() {
    super("response.notHandled", "Unknown command or text, try something else");
  }

  @Override
  public void process(UpdateRequest updateRequest) throws TelegramApiException {
    String text = getMessageForLocale(updateRequest);
    sendString(text, updateRequest);
  }
}
