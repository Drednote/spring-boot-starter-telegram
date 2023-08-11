package io.github.drednote.telegram.menu;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram.menu")
@Getter
@Setter
public class MenuProperties {

  private static final Pattern PATTERN = Pattern.compile("[^a-z\\s/]");

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
        validate(command);
        this.command = (command.startsWith("/") ? "" : "/") + command;
      }
    }

    /**
     * Valid only lower case and '/' symbol
     *
     * @param command command
     */
    void validate(String command) {
      Matcher matcher = PATTERN.matcher(command);
      if (matcher.find() || command.lastIndexOf('/') > 0) {
        throw new IllegalArgumentException(
            "Bot command must contain only lower case letters and '/' symbol as first symbol");
      }
    }
  }

  public enum SendPolicy {
    NONE, ON_STARTUP
  }
}
