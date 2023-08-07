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
    FilterProperties.class, PermissionProperties.class, MenuProperties.class
})
@Getter
@Setter
public class TelegramProperties {

  private String name;
  private String token;
  private String defaultLocale;
  private SessionProperties session = new SessionProperties();
  private UpdateHandlerProperties updateHandler = new UpdateHandlerProperties();
  private DataSourceProperties dataSource = new DataSourceProperties();
  private FilterProperties filters = new FilterProperties();
  private PermissionProperties permission = new PermissionProperties();
  private MenuProperties menu = new MenuProperties();
}
