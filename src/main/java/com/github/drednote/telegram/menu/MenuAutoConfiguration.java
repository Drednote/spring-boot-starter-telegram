package com.github.drednote.telegram.menu;

import com.github.drednote.telegram.menu.MenuProperties.SendPolicy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(MenuProperties.class)
@RequiredArgsConstructor
public class MenuAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

  private final MenuProperties properties;
  private final AbsSender absSender;
  private final ObjectProvider<BotMenu> menuProvider;

  @Bean
  @ConditionalOnMissingBean
  public BotMenu botMenu() {
    return new BotMenuImpl(properties.getValues());
  }

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
