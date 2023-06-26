package com.github.drednote.telegram;

import com.github.drednote.telegram.bot.TelegramBotAutoConfiguration;
import com.github.drednote.telegram.session.SessionAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ImportAutoConfiguration({SessionAutoConfiguration.class, TelegramBotAutoConfiguration.class})
@EnableConfigurationProperties(TelegramProperties.class)
@AutoConfiguration
public class TelegramAutoConfiguration {
}
