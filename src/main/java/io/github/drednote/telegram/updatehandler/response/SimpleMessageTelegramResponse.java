package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import java.util.Locale;
import java.util.Optional;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Abstract base class for implementing simple Telegram response actions that involve sending a text
 * message.
 *
 * @author Ivan Galushko
 */
public abstract class SimpleMessageTelegramResponse extends AbstractTelegramResponse {

  private final String defaultMessage;
  private final String code;
  @Setter
  @Nullable
  private TelegramMessageSource messageSource;

  /**
   * Constructs an instance of SimpleMessageTelegramResponse with optional code and default
   * message.
   *
   * @param code           The code to look up in the message source for the response message
   * @param defaultMessage The default message to use if the code is not found in the message
   *                       source
   */
  protected SimpleMessageTelegramResponse(String code, String defaultMessage) {
    this.code = code;
    this.defaultMessage = defaultMessage;
  }

  /**
   * Retrieves the message for the locale of the provided update request.
   *
   * @param request The update request to get the user locale from
   * @return The retrieved message for the locale, or the default message if unavailable
   */
  @Nullable
  protected String getMessageForLocale(TelegramUpdateRequest request) {
    if (messageSource != null) {
      User user = request.getUser();
      Locale locale = Optional.ofNullable(user)
          .map(User::getLanguageCode)
          .map(Locale::forLanguageTag)
          .orElse(messageSource.getDefaultLocale());
      String message = messageSource.getMessage(code, null, defaultMessage, locale);
      return message != null ? message : defaultMessage;
    }
    return defaultMessage;
  }

  /**
   * Sends a text message with the retrieved message for the locale of the update request.
   *
   * @param request The update request containing the chat information
   * @throws TelegramApiException if sending the message fails
   */
  @Override
  public void process(TelegramUpdateRequest request) throws TelegramApiException {
    String text = getMessageForLocale(request);
    if (text != null) {
      sendString(text, request);
    }
  }
}
