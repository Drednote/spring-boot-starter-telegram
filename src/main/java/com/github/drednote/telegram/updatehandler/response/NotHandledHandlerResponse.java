package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.request.BotRequest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class NotHandledHandlerResponse extends AbstractHandlerResponse {

  public static final NotHandledHandlerResponse INSTANCE = new NotHandledHandlerResponse();

  private NotHandledHandlerResponse() {
    super("response.notHandled", "Unknown command or text, try something else");
  }

  @Override
  public void process(BotRequest request) throws TelegramApiException {
    String text = getMessageForLocale(request);
    sendString(text, request);
  }
}
