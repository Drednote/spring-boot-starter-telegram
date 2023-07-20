package com.github.drednote.telegram.menu;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram-bot.menu")
@Getter
@Setter
public class MenuProperties {

  /**
   * Create bean {@link BotMenu} with this commands
   */
  private Map<String, CommandCls> values;
  /**
   * Send policy
   */
  private SendPolicy sendPolicy = SendPolicy.ON_STARTUP;

  @Getter
  @Setter
  public static class CommandCls {

    /**
     * Текст кнопки. Пример - Регистрация
     * todo add localization
     */
    private String text;

    /**
     * Команда. Пример - /register
     */
    private String command;

    public void setCommand(String command) {
      if (command != null) {
        this.command = (command.startsWith("/") ? "" : "/") + command;
      }
    }
  }

  public enum SendPolicy {
    NONE, ON_STARTUP, SCHEDULED
  }
}
