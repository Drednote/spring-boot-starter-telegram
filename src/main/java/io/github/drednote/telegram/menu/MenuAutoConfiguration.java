package io.github.drednote.telegram.menu;

import io.github.drednote.telegram.menu.MenuProperties.SendPolicy;
import io.github.drednote.telegram.utils.Assert;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Autoconfiguration class for managing bot menus and commands.
 *
 * <p>It implements {@link ApplicationListener<ApplicationReadyEvent>} to listen for the
 * application's ready event and manage bot menu updates based on the provided properties.
 *
 * <p>When the application is ready, this class updates the bot menu with commands if the send
 * policy is set to {@link SendPolicy#ON_STARTUP}. It uses an {@link AbsSender} to send the commands
 * to the bot.
 *
 * @author Ivan Galushko
 * @see BotMenu
 * @see MenuProperties
 */
@AutoConfiguration
@EnableConfigurationProperties(MenuProperties.class)
public class MenuAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger log = LoggerFactory.getLogger(MenuAutoConfiguration.class);

  private final MenuProperties properties;
  private final AbsSender absSender;
  private final ObjectProvider<BotMenu> menuProvider;

  public MenuAutoConfiguration(
      MenuProperties properties, AbsSender absSender, ObjectProvider<BotMenu> menuProvider
  ) {
    Assert.required(properties, "MenuProperties");
    Assert.required(absSender, "AbsSender");
    Assert.required(menuProvider, "BotMenu provider");

    this.properties = properties;
    this.absSender = absSender;
    this.menuProvider = menuProvider;
  }

  /**
   * Creates a bean for the {@link BotMenu} implementation.
   *
   * @return A bean of type {@code BotMenu}.
   */
  @Bean
  @ConditionalOnMissingBean
  public BotMenu botMenu() {
    return new BotMenuImpl(properties.getValues());
  }

  /**
   * Handles the application ready event to update the bot menu.
   *
   * <p>This method is triggered when the application is ready. If the send policy is set to
   * {@link SendPolicy#ON_STARTUP}, it retrieves the bot menu from the menu provider and sends the
   * menu commands to the bot using an {@link AbsSender}. If the menu is empty, a log message is
   * generated.
   *
   * @param event The application ready event
   */
  @Override
  public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
    if (properties.getSendPolicy() == SendPolicy.ON_STARTUP) {
      BotMenu botMenu = menuProvider.getObject();
      if (botMenu.isEmpty()) {
        log.info("Skip menu update due to empty commands list");
      } else {
        SetMyCommands setMyCommands = new SetMyCommands();
        List<BotCommand> commands = botMenu.getCommands();
        setMyCommands.setCommands(commands);
        try {
          absSender.execute(setMyCommands);
          log.info("Update menu with commands {}", commands);
        } catch (Exception e) {
          log.error("Skip menu update due to error", e);
        }
      }
    }
  }
}
