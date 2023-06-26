package com.github.drednote.telegram;

import com.github.drednote.telegram.session.SessionProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram-bot")
@EnableConfigurationProperties(SessionProperties.class)
@Data
public class TelegramProperties {

  private String name;
  private String token;
  private SessionProperties session;
}
