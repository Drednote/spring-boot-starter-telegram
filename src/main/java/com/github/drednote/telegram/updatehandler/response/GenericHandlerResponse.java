package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class GenericHandlerResponse extends AbstractHandlerResponse {

  private final Object response;

  @Override
  public void process(UpdateRequest updateRequest) throws TelegramApiException, IOException {
    if (response instanceof String str) {
      sendString(str, updateRequest);
    } else if (response instanceof byte[] bytes) {
      sendString(new String(bytes, StandardCharsets.UTF_16), updateRequest);
    } else {
      String stringResponse = updateRequest.getObjectMapper().writeValueAsString(response);
      sendString(stringResponse, updateRequest);
    }
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
