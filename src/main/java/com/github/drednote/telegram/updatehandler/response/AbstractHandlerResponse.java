package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.BotMessageSource;
import com.github.drednote.telegram.core.request.BotRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import java.util.Locale;
import java.util.Optional;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
  protected String getMessageForLocale(BotRequest request) {
    if (code != null && messageSource != null) {
      User user = request.getUser();
      Locale locale = Optional.ofNullable(user)
          .map(User::getLanguageCode)
          .map(Locale::forLanguageTag)
          .orElse(messageSource.getDefaultLocale());
      return messageSource.getMessage(code, null, defaultMessage, locale);
    }
    return defaultMessage;
  }

  protected void sendString(String string, BotRequest request) throws TelegramApiException {
    AbsSender absSender = request.getAbsSender();
    Long chatId = request.getChatId();
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText(string);
    absSender.execute(sendMessage);
  }
}
