package com.github.drednote.telegram.updatehandler.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.drednote.telegram.core.UpdateRequest;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class GenericHandlerResponse extends AbstractHandlerResponse {

  @NonNull
  private final Object response;

  @Override
  public void process(UpdateRequest updateRequest) throws TelegramApiException {
    if (response instanceof String str) {
      sendString(str, updateRequest);
    } else if (response instanceof byte[] bytes) {
      sendString(new String(bytes, StandardCharsets.UTF_16), updateRequest);
    } else if (response instanceof BotApiMethod<?> botApiMethod) {
      updateRequest.getAbsSender().execute(botApiMethod);
    } else {
      try {
        String stringResponse = updateRequest.getObjectMapper().writeValueAsString(response);
        sendString(stringResponse, updateRequest);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Cannot serialize response", e);
      }
    }
  }
}
