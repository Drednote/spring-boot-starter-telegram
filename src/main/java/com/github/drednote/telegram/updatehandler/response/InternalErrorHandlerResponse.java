package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class InternalErrorHandlerResponse extends AbstractHandlerResponse {

  @Override
  public void process(UpdateRequest updateRequest) throws TelegramApiException {
    String text = "Упс, что-то пошло не так, попробуйте позже";
    sendString(text, updateRequest);
  }
}
