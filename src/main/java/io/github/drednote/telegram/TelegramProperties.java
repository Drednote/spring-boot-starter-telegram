package io.github.drednote.telegram;

import io.github.drednote.telegram.datasource.DataSourceProperties;
import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.filter.PermissionProperties;
import io.github.drednote.telegram.menu.MenuProperties;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.updatehandler.UpdateHandlerProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram")
@EnableConfigurationProperties({
    SessionProperties.class, UpdateHandlerProperties.class, DataSourceProperties.class,
    FilterProperties.class, MenuProperties.class
})
@Getter
@Setter
public class TelegramProperties {

  /**
   * The name of a bot. Example: TheBestBot.
   * <p>
   * <b>Required</b>
   */
  private String name;
  /**
   * The token of a bot.
   * <p>
   * <b>Required</b>
   */
  private String token;
  /**
   * The default locale with which bot will send responses to user chats. Must be two symbols.
   * Example: en, fr, ru.
   *
   * @see java.util.Locale
   */
  private String defaultLocale;
  /**
   * Session properties
   */
  private SessionProperties session = new SessionProperties();
  /**
   * Properties of update handlers
   */
  private UpdateHandlerProperties updateHandler = new UpdateHandlerProperties();
  /**
   * Datasource properties
   */
  private DataSourceProperties dataSource = new DataSourceProperties();
  /**
   * Filters properties
   */
  private FilterProperties filters = new FilterProperties();
  /**
   * Menu properties
   */
  private MenuProperties menu = new MenuProperties();
}
