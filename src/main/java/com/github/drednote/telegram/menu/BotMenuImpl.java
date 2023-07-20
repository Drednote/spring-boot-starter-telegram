package com.github.drednote.telegram.menu;

import com.github.drednote.telegram.menu.MenuProperties.CommandCls;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public final class BotMenuImpl implements BotMenu {

  private final Map<String, BotCommand> commands;

  public BotMenuImpl(Map<String, CommandCls> commands) {
    this.commands = commands.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey,
            it -> new BotCommand(it.getValue().getCommand(), it.getValue().getText())));
  }

  @Override
  public List<BotCommand> getCommands() {
    return new ArrayList<>(commands.values());
  }

  @Override
  public boolean isEmpty() {
    return commands.isEmpty();
  }
}
