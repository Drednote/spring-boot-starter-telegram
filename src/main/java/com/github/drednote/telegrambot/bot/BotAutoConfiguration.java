package com.github.drednote.telegrambot.bot;

import com.github.drednote.telegrambot.TelegramBotProperties;
import com.github.drednote.telegrambot.session.SessionProperties.UpdateStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.generics.TelegramBot;

@AutoConfiguration
public class BotAutoConfiguration {

  @Bean(destroyMethod = "onClosing")
  @ConditionalOnProperty(
      prefix = "drednote.telegram-bot.session",
      name = "type",
      havingValue = "LONG_POLLING",
      matchIfMissing = true
  )
  @ConditionalOnMissingBean(TelegramBot.class)
  public TelegramLongPollingBot telegramLongPollingBot(TelegramBotProperties properties) {
    if (StringUtils.isBlank(properties.getToken())) {
      throw new BeanCreationException("TelegramBot",
          "Consider specify drednote.telegram-bot.token");
    }
    if (StringUtils.isBlank(properties.getName())) {
      throw new BeanCreationException("TelegramBot",
          "Consider specify drednote.telegram-bot.name");
    }
    if (properties.getSession().getUpdateStrategy() == UpdateStrategy.LONG_POLLING) {
      return new LongPollingBotContainer(properties);
    } else {
      throw new UnsupportedOperationException("Webhooks not implemented yet");
    }
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "drednote.telegram-bot.session",
      name = "type",
      havingValue = "WEBHOOKS"
  )
  @ConditionalOnMissingBean(TelegramBot.class)
  public TelegramWebhookBot telegramWebhookBot() {
    throw new UnsupportedOperationException("Webhooks not implemented yet");
  }
}
