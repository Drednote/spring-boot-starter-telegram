package com.github.drednote.telegram.updatehandler.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.drednote.telegram.core.request.BotRequest;
import com.github.drednote.telegram.core.request.ExtendedBotRequest;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class GenericHandlerResponse extends AbstractHandlerResponse {

  @NonNull
  private final Object response;

  public GenericHandlerResponse(@NonNull Object response) {
    super(null, null);
    this.response = response;
  }

  @Override
  public void process(BotRequest request) throws TelegramApiException {
    if (response instanceof String str) {
      sendString(str, request);
    } else if (response instanceof byte[] bytes) {
      sendString(new String(bytes, StandardCharsets.UTF_16), request);
    } else if (response instanceof BotApiMethod<?> botApiMethod) {
      request.getAbsSender().execute(botApiMethod);
    } else if (request instanceof ExtendedBotRequest extendedBotRequest) {
      try {
        String stringResponse = extendedBotRequest.getObjectMapper().writeValueAsString(response);
        sendString(stringResponse, request);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Cannot serialize response", e);
      }
    } else {
      log.error("Cannot process response {}", response);
    }
  }
}
