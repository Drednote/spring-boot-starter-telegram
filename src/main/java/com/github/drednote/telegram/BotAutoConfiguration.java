package com.github.drednote.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drednote.telegram.core.LongPollingBot;
import com.github.drednote.telegram.exception.ExceptionHandler;
import com.github.drednote.telegram.filter.UpdateFilterProvider;
import com.github.drednote.telegram.session.SessionProperties.UpdateStrategy;
import com.github.drednote.telegram.updatehandler.UpdateHandler;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.generics.TelegramBot;

@AutoConfiguration
public class BotAutoConfiguration {

  private static final String TELEGRAM_BOT = "TelegramBot";

  @Bean(destroyMethod = "onClosing")
  @ConditionalOnMissingBean(TelegramBot.class)
  public TelegramLongPollingBot telegramLongPollingBot(
      TelegramProperties properties, Collection<UpdateHandler> updateHandlers,
      ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
      UpdateFilterProvider updateFilterProvider
  ) {
    if (StringUtils.isBlank(properties.getToken())) {
      throw new BeanCreationException(TELEGRAM_BOT,
          "Consider specify drednote.telegram-bot.token");
    }
    if (StringUtils.isBlank(properties.getName())) {
      throw new BeanCreationException(TELEGRAM_BOT,
          "Consider specify drednote.telegram-bot.name");
    }
    if (properties.getSession().getUpdateStrategy() == UpdateStrategy.LONG_POLLING) {
      return new LongPollingBot(properties, updateHandlers, objectMapper,
          exceptionHandler, updateFilterProvider);
    } else {
      throw new BeanCreationException(TELEGRAM_BOT, "Webhooks not implemented yet");
    }
  }
}
