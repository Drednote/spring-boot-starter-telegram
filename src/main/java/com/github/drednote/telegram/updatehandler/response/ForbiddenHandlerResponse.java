package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class ForbiddenHandlerResponse extends AbstractHandlerResponse {

  @Override
  public void process(UpdateRequest updateRequest) throws TelegramApiException {
    String text = "У вас нет доступа к этому боту";
    sendString(text, updateRequest);
  }
}
