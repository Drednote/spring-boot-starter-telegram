package io.github.drednote.telegram.updatehandler.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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

/**
 * This class represents a generic Telegram response handler that can process various types of
 * responses and messages. It extends the {@code AbstractTelegramResponse} class and implements the
 * {@link TelegramResponse} interface. The response can be a String, byte array, BotApiMethod,
 * SendMediaBotMethod, TelegramResponse, or any serializable object. Depending on the type of
 * response, it will be processed accordingly.
 * <p>
 * It is the main {@code TelegramResponse} implementation that should be used manually in code
 * <p>
 * todo add realization for collection of TelegramResponse
 *
 * @author Ivan Galushko
 */
public class GenericTelegramResponse extends AbstractTelegramResponse {

  /**
   * The response object to be processed
   */
  @NonNull
  private final Object response;

  /**
   * Creates a new instance of GenericTelegramResponse with the specified response object.
   *
   * @param response The response object to be processed
   */
  public GenericTelegramResponse(@NonNull Object response) {
    Assert.required(response, "Response");
    this.response = response;
  }

  /**
   * Processes the Telegram response. Depending on the type of response, it will be sent to the
   * chat.
   *
   * @param request The TelegramUpdateRequest containing the update and sender information
   * @throws TelegramApiException if an error occurs while sending the response
   */
  @Override
  public void process(TelegramUpdateRequest request) throws TelegramApiException {
    Assert.notNull(request, "TelegramUpdateRequest");
    Serializable responseMessage;
    if (response instanceof String str) {
      responseMessage = sendString(str, request);
    } else if (response instanceof byte[] bytes) {
      responseMessage = sendString(new String(bytes, StandardCharsets.UTF_8), request);
    } else if (response instanceof BotApiMethod<?> botApiMethod) {
      responseMessage = request.getAbsSender().execute(botApiMethod);
    } else if (response instanceof SendMediaBotMethod<?>) {
      responseMessage = tryToSendMedia(request);
    } else if (response instanceof TelegramResponse telegramResponse) {
      telegramResponse.process(request);
      responseMessage = null;
    } else if (request.getProperties().getUpdateHandler().isSerializeJavaObjectWithJackson()) {
      try {
        String stringResponse = request.getObjectMapper().writeValueAsString(response);
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

  @Nullable
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

  /**
   * Handles the processed response message, allowing for further actions if needed.
   *
   * @param response The response message to be handled
   */
  protected void handleResponseMessage(@NonNull Serializable response) {
    // do something if needed
  }
}
