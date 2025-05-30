package io.github.drednote.telegram;

import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.handler.UpdateHandlerProperties;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties;
import io.github.drednote.telegram.menu.MenuProperties;
import io.github.drednote.telegram.session.SessionProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Configuration
@ConfigurationProperties("drednote.telegram")
@EnableConfigurationProperties({
    SessionProperties.class, UpdateHandlerProperties.class,
    FilterProperties.class, MenuProperties.class, ScenarioProperties.class
})
@Getter
@Setter
public class TelegramProperties {
  /**
   * Enable the bot. Default: true
   */
  private boolean enabled = true;
  /**
   * The name of a bot. Example: TheBestBot.
   * <p>
   * @deprecated not used since 0.3.0
   */
  @Nullable
  @Deprecated(since = "0.3.0")
  private String name;
  /**
   * The token of a bot.
   * <p>
   * <b>Required</b>
   */
  @NonNull
  private String token;
  /**
   * The default locale with which bot will send responses to user chats. A two-letter ISO 639-1
   * language code
   * <p>
   * Example: en, fr, ru.
   *
   * @see java.util.Locale
   */
  @Nullable
  private String defaultLocale;
  /**
   * Session properties
   */
  @NonNull
  private SessionProperties session = new SessionProperties();
  /**
   * Properties of update handlers
   */
  @NonNull
  private UpdateHandlerProperties updateHandler = new UpdateHandlerProperties();
  /**
   * Filters properties
   */
  @NonNull
  private FilterProperties filters = new FilterProperties();
  /**
   * Menu properties
   */
  @NonNull
  private MenuProperties menu = new MenuProperties();
  /**
   * Scenario properties
   */
  @NonNull
  private ScenarioProperties scenario = new ScenarioProperties();
}
