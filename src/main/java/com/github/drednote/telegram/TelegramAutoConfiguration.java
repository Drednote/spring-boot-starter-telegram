package com.github.drednote.telegram;

import com.github.drednote.telegram.bot.BotContainerAutoConfiguration;
import com.github.drednote.telegram.session.SessionAutoConfiguration;
import com.github.drednote.telegram.updatehandler.UpdateHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ImportAutoConfiguration({
    SessionAutoConfiguration.class, BotContainerAutoConfiguration.class,
    UpdateHandlerAutoConfiguration.class
})
@EnableConfigurationProperties(TelegramProperties.class)
@AutoConfiguration
public class TelegramAutoConfiguration {
}
