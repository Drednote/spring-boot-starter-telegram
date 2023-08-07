package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.BotMessageSource;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.HandlerResponse;
import java.util.Locale;
import java.util.Optional;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbstractHandlerResponse implements HandlerResponse {

  private final String defaultMessage;
  private final String code;
  @Setter
  private BotMessageSource messageSource;

  protected AbstractHandlerResponse(String code, String defaultMessage) {
    this.code = code;
    this.defaultMessage = defaultMessage;
  }

  @Nullable
  protected String getMessageForLocale(TelegramUpdateRequest request) {
    if (code != null && messageSource != null) {
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

  protected Message sendString(String string, TelegramUpdateRequest request) throws TelegramApiException {
    AbsSender absSender = request.getAbsSender();
    Long chatId = request.getChatId();
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText(string);
    return absSender.execute(sendMessage);
  }
}
