package com.github.drednote.telegram;

import com.github.drednote.telegram.exception.ExceptionHandlerAutoConfiguration;
import com.github.drednote.telegram.session.SessionAutoConfiguration;
import com.github.drednote.telegram.updatehandler.UpdateHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ImportAutoConfiguration({
    BotAutoConfiguration.class, SessionAutoConfiguration.class,
    UpdateHandlerAutoConfiguration.class, ExceptionHandlerAutoConfiguration.class,
})
@EnableConfigurationProperties(TelegramProperties.class)
@AutoConfiguration
public class TelegramAutoConfiguration {

}
