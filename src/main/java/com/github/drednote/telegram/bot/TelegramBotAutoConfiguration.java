package com.github.drednote.telegram.bot;

import com.github.drednote.telegram.TelegramProperties;
import com.github.drednote.telegram.session.SessionProperties.UpdateStrategy;
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
public class TelegramBotAutoConfiguration {

  private static final String TELEGRAM_BOT = "TelegramBot";

  @Bean(destroyMethod = "onClosing")
  @ConditionalOnProperty(
      prefix = "drednote.telegram-bot.session",
      name = "type",
      havingValue = "LONG_POLLING",
      matchIfMissing = true
  )
  @ConditionalOnMissingBean(TelegramBot.class)
  public TelegramLongPollingBot telegramLongPollingBot(TelegramProperties properties) {
    if (StringUtils.isBlank(properties.getToken())) {
      throw new BeanCreationException(TELEGRAM_BOT,
          "Consider specify drednote.telegram-bot.token");
    }
    if (StringUtils.isBlank(properties.getName())) {
      throw new BeanCreationException(TELEGRAM_BOT,
          "Consider specify drednote.telegram-bot.name");
    }
    if (properties.getSession().getUpdateStrategy() == UpdateStrategy.LONG_POLLING) {
      return new LongPollingBotContainer(properties);
    } else {
      throw new BeanCreationException(TELEGRAM_BOT, "Webhooks not implemented yet");
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
    throw new BeanCreationException(TELEGRAM_BOT, "Webhooks not implemented yet");
  }
}
