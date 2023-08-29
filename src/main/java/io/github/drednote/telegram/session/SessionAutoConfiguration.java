package io.github.drednote.telegram.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.generics.TelegramBot;

/**
 * Autoconfiguration class for managing Telegram bot sessions and scopes.
 *
 * <p>This class provides automatic configuration for different types of Telegram bot sessions,
 * including long polling and webhooks, based on properties defined in the application's
 * configuration. It also configures custom scopes to manage beans associated with Telegram bot
 * requests.
 */
@AutoConfiguration
@EnableConfigurationProperties(SessionProperties.class)
public class SessionAutoConfiguration {

  /**
   * Configures a bean for the Telegram bot session using long polling. And starts session
   *
   * @param telegramClient The Telegram client used to interact with the Telegram API
   * @param bot            The Telegram bot instance
   * @param properties     Configuration properties for the session
   * @return The configured Telegram bot session
   */
  @Bean(destroyMethod = "stop")
  @ConditionalOnProperty(
      prefix = "drednote.telegram.session",
      name = "type",
      havingValue = "LONG_POLLING",
      matchIfMissing = true
  )
  @ConditionalOnMissingBean
  @ConditionalOnSingleCandidate(TelegramBot.class)
  public TelegramBotSession longPollingTelegramBotSession(
      TelegramClient telegramClient, TelegramBot bot, SessionProperties properties
  ) {
    LongPollingSession session = new LongPollingSession(telegramClient, properties, bot);
    session.start();
    return session;
  }

  /**
   * Configures a bean for the Telegram bot session using webhooks.
   *
   * <p><b>Throws {@link UnsupportedOperationException} because webhooks are not yet
   * implemented</b>
   *
   * @return The configured Telegram bot session
   */
  @Bean(destroyMethod = "stop")
  @ConditionalOnProperty(
      prefix = "drednote.telegram.session",
      name = "type",
      havingValue = "WEBHOOKS"
  )
  @ConditionalOnMissingBean
  public TelegramBotSession webhooksTelegramBotSession() {
    throw new UnsupportedOperationException("Webhooks not implemented yet");
  }

  /**
   * Configures a custom scope for managing Telegram bot request beans.
   *
   * @return The configured CustomScopeConfigurer bean
   */
  @Bean
  public CustomScopeConfigurer customScopeConfigurer() {
    CustomScopeConfigurer configurer = new CustomScopeConfigurer();
    configurer.addScope(TelegramRequestScope.BOT_SCOPE_NAME, new TelegramRequestScope());
    return configurer;
  }

  /**
   * Configures a bean for the Telegram client to interact with the Telegram API.
   *
   * @return The configured Telegram client
   */
  @Bean
  @ConditionalOnMissingBean
  public TelegramClient telegramClient(SessionProperties properties, ObjectMapper objectMapper) {
    return new TelegramClientImpl(properties, objectMapper);
  }

  /**
   * Configures a bean for managing the Telegram bot request context.
   *
   * @return The configured UpdateRequestContext bean
   */
  @Bean
  public UpdateRequestContext botSessionContext() {
    return new UpdateRequestContext() {};
  }
}
