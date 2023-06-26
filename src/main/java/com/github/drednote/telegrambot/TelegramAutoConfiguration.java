package com.github.drednote.telegrambot;

import com.github.drednote.telegrambot.bot.BotAutoConfiguration;
import com.github.drednote.telegrambot.session.SessionAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ImportAutoConfiguration({SessionAutoConfiguration.class, BotAutoConfiguration.class})
@EnableConfigurationProperties(TelegramBotProperties.class)
@AutoConfiguration
public class TelegramAutoConfiguration {
}
