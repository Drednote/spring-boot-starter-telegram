package io.github.drednote.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.core.CoreAutoConfiguration;
import io.github.drednote.telegram.core.DefaultTelegramBot;
import io.github.drednote.telegram.core.TelegramBot;
import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.datasource.DataSourceAutoConfiguration;
import io.github.drednote.telegram.exception.ExceptionHandler;
import io.github.drednote.telegram.exception.ExceptionHandlerAutoConfiguration;
import io.github.drednote.telegram.filter.FiltersAutoConfiguration;
import io.github.drednote.telegram.filter.UpdateFilterProvider;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.handler.UpdateHandlerAutoConfiguration;
import io.github.drednote.telegram.menu.MenuAutoConfiguration;
import io.github.drednote.telegram.session.SessionAutoConfiguration;
import io.github.drednote.telegram.session.SessionProperties.UpdateStrategy;
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
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Autoconfiguration class for configuring various aspects of a Telegram bot application.
 *
 * <p>This class provides automatic configuration for different components and features of a
 * Telegram bot application, including bot configuration, message source configuration, and more. It
 * utilizes properties defined in the application's configuration to customize the behavior of the
 * bot.
 */
@ImportAutoConfiguration({
    SessionAutoConfiguration.class, UpdateHandlerAutoConfiguration.class,
    ExceptionHandlerAutoConfiguration.class, DataSourceAutoConfiguration.class,
    FiltersAutoConfiguration.class, MenuAutoConfiguration.class, CoreAutoConfiguration.class
})
@EnableConfigurationProperties(TelegramProperties.class)
@AutoConfiguration
public class TelegramAutoConfiguration {

    private static final String TELEGRAM_BOT = "TelegramBot";

    public TelegramAutoConfiguration(TelegramProperties properties) {
        if (StringUtils.isBlank(properties.getToken())) {
            throw new BeanCreationException(TELEGRAM_BOT,
                "If you want to use telegram bot library consider specify a drednote.telegram.token or disable creating "
                + "telegram bot by setting a drednote.telegram.enabled to false");
        }
    }

    /**
     * Autoconfiguration class for configuring the Telegram bot instance.
     */
    @AutoConfiguration
    public static class BotConfig {


        /**
         * Configures a bean for the Telegram bot instance.
         *
         * @param properties           Configuration properties for the Telegram bot
         * @param updateHandlers       Collection of update handlers for processing incoming
         *                             updates
         * @param objectMapper         The ObjectMapper instance used for serialization and
         *                             deserialization
         * @param exceptionHandler     The ExceptionHandler instance for handling exceptions
         * @param updateFilterProvider The UpdateFilterProvider instance for filtering updates
         * @param messageSource        The TelegramMessageSource instance for retrieving localized
         *                             messages
         * @return The configured Telegram bot instance
         * @throws BeanCreationException When bot token or bot name are missing
         * @apiNote WebHooks not implemented yet
         */
        @Bean
        @ConditionalOnMissingBean(TelegramBot.class)
        public TelegramBot telegramLongPollingBot(
            TelegramProperties properties, Collection<UpdateHandler> updateHandlers,
            ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
            UpdateFilterProvider updateFilterProvider, TelegramMessageSource messageSource,
            TelegramClient telegramClient
        ) {
            if (StringUtils.isBlank(properties.getToken())) {
                throw new BeanCreationException(TELEGRAM_BOT,
                    "Consider specify drednote.telegram.token");
            }
            if (properties.getSession().getUpdateStrategy() == UpdateStrategy.LONG_POLLING) {
                return new DefaultTelegramBot(properties, updateHandlers, objectMapper,
                    exceptionHandler, updateFilterProvider, messageSource, telegramClient);
            } else {
                throw new BeanCreationException(TELEGRAM_BOT, "Webhooks not implemented yet");
            }
        }
    }

    /**
     * Autoconfiguration class for configuring the message source for the Telegram bot.
     */
    @AutoConfiguration
    @AutoConfigureBefore(BotConfig.class)
    public static class LocaleConfig {

        /**
         * Configures a bean for the TelegramMessageSource to retrieve localized messages.
         *
         * @param properties Configuration properties for the Telegram bot
         * @return The configured TelegramMessageSource instance
         */
        @Bean
        public TelegramMessageSource botMessageSource(TelegramProperties properties) {
            var messageSource = new TelegramMessageSource();
            messageSource.setBasename("classpath:telegram-messages");
            messageSource.setDefaultEncoding("UTF-8");
            if (properties.getDefaultLocale() != null) {
                messageSource.setDefaultLocale(
                    Locale.forLanguageTag(properties.getDefaultLocale()));
            }
            return messageSource;
        }
    }
}
