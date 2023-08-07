package io.github.drednote.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.core.LongPollingBot;
import io.github.drednote.telegram.datasource.DataSourceAutoConfiguration;
import io.github.drednote.telegram.exception.ExceptionHandler;
import io.github.drednote.telegram.exception.ExceptionHandlerAutoConfiguration;
import io.github.drednote.telegram.filter.FiltersAutoConfiguration;
import io.github.drednote.telegram.filter.UpdateFilterProvider;
import io.github.drednote.telegram.menu.MenuAutoConfiguration;
import io.github.drednote.telegram.session.SessionAutoConfiguration;
import io.github.drednote.telegram.session.SessionProperties.UpdateStrategy;
import io.github.drednote.telegram.updatehandler.UpdateHandler;
import io.github.drednote.telegram.updatehandler.UpdateHandlerAutoConfiguration;
import java.util.Collection;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.generics.TelegramBot;

@ImportAutoConfiguration({
    SessionAutoConfiguration.class, UpdateHandlerAutoConfiguration.class,
    ExceptionHandlerAutoConfiguration.class, DataSourceAutoConfiguration.class,
    FiltersAutoConfiguration.class, MenuAutoConfiguration.class
})
@EnableConfigurationProperties(TelegramProperties.class)
@AutoConfiguration
public class TelegramAutoConfiguration {

  @AutoConfiguration
  public static class BotConfig {

    private static final String TELEGRAM_BOT = "TelegramBot";

    @Bean(destroyMethod = "onClosing")
    @ConditionalOnMissingBean(TelegramBot.class)
    public TelegramLongPollingBot telegramLongPollingBot(
        TelegramProperties properties, Collection<UpdateHandler> updateHandlers,
        ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
        UpdateFilterProvider updateFilterProvider, TelegramMessageSource messageSource
    ) {
      if (StringUtils.isBlank(properties.getToken())) {
        throw new BeanCreationException(TELEGRAM_BOT,
            "Consider specify drednote.telegram.token");
      }
      if (StringUtils.isBlank(properties.getName())) {
        throw new BeanCreationException(TELEGRAM_BOT,
            "Consider specify drednote.telegram.name");
      }
      if (properties.getSession().getUpdateStrategy() == UpdateStrategy.LONG_POLLING) {
        return new LongPollingBot(properties, updateHandlers, objectMapper,
            exceptionHandler, updateFilterProvider, messageSource);
      } else {
        throw new BeanCreationException(TELEGRAM_BOT, "Webhooks not implemented yet");
      }
    }
  }

  @AutoConfiguration
  @AutoConfigureBefore(BotConfig.class)
  public static class LocaleConfig {

    @Bean
    public TelegramMessageSource botMessageSource(TelegramProperties properties) {
      var messageSource = new TelegramMessageSource();
      messageSource.setBasename("classpath:telegram-messages");
      messageSource.setDefaultEncoding("UTF-8");
      if (properties.getDefaultLocale() != null) {
        messageSource.setDefaultLocale(Locale.forLanguageTag(properties.getDefaultLocale()));
      }
      return messageSource;
    }
  }
}
