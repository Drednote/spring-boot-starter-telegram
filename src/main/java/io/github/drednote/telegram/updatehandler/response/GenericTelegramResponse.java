package io.github.drednote.telegram.updatehandler.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaBotMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class GenericTelegramResponse extends AbstractTelegramResponse {

  @NonNull
  private final Object response;

  public GenericTelegramResponse(@NonNull Object response) {
    super(null, null);
    this.response = response;
  }

  @Override
  public void process(TelegramUpdateRequest request) throws TelegramApiException {
    Serializable responseMessage;
    if (response instanceof String str) {
      responseMessage = sendString(str, request);
    } else if (response instanceof byte[] bytes) {
      responseMessage = sendString(new String(bytes, StandardCharsets.UTF_8), request);
    } else if (response instanceof BotApiMethod<?> botApiMethod) {
      responseMessage = request.getAbsSender().execute(botApiMethod);
    } else if (response instanceof SendMediaBotMethod<?>) {
      responseMessage = tryToSendMedia(request);
    } else if (request instanceof ExtendedTelegramUpdateRequest extendedBotRequest) {
      try {
        String stringResponse = extendedBotRequest.getObjectMapper().writeValueAsString(response);
        String truncated = truncateQuotes(stringResponse);
        responseMessage = sendString(truncated, request);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Cannot serialize response", e);
      }
    } else {
      throw new IllegalStateException("Cannot process response %s".formatted(response));
    }
    if (responseMessage != null) {
      handleResponseMessage(responseMessage);
    }
  }

  private String truncateQuotes(String stringResponse) {
    if (!StringUtils.isBlank(stringResponse) && (stringResponse.startsWith("\"")
        && stringResponse.endsWith("\""))) {
      return stringResponse.substring(1, stringResponse.length() - 1);
    }
    return stringResponse;
  }

  private Serializable tryToSendMedia(TelegramUpdateRequest request) throws TelegramApiException {
    if (response instanceof SendAnimation sendAnimation) {
      return request.getAbsSender().execute(sendAnimation);
    }
    if (response instanceof SendAudio sendAudio) {
      return request.getAbsSender().execute(sendAudio);
    }
    if (response instanceof SendDocument sendDocument) {
      return request.getAbsSender().execute(sendDocument);
    }
    if (response instanceof SendPhoto sendPhoto) {
      return request.getAbsSender().execute(sendPhoto);
    }
    if (response instanceof SendSticker sendSticker) {
      return request.getAbsSender().execute(sendSticker);
    }
    if (response instanceof SendVideo sendVideo) {
      return request.getAbsSender().execute(sendVideo);
    }
    if (response instanceof SendVideoNote sendVideoNote) {
      return request.getAbsSender().execute(sendVideoNote);
    }
    if (response instanceof SendVoice sendVoice) {
      return request.getAbsSender().execute(sendVoice);
    }
    return null;
  }

  protected void handleResponseMessage(@NonNull Serializable response) {
    // do something if needed
  }
}
