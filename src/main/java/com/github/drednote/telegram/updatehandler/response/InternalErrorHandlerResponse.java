package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class InternalErrorHandlerResponse extends AbstractHandlerResponse {

  public static final InternalErrorHandlerResponse INSTANCE = new InternalErrorHandlerResponse();

  private InternalErrorHandlerResponse() {
    super("response.internalError", "Oops, something went wrong, please try again later.");
  }

  @Override
  public void process(UpdateRequest updateRequest) throws TelegramApiException {
    String text = getMessageForLocale(updateRequest);
    sendString(text, updateRequest);
  }
}
