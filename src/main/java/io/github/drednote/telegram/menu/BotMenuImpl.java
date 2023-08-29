package io.github.drednote.telegram.menu;

import io.github.drednote.telegram.menu.MenuProperties.CommandCls;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

/**
 * Implementation of the {@link BotMenu} interface for managing bot commands.
 *
 * <p>This class implements the {@link BotMenu} interface to manage a collection of bot commands.
 * It provides methods for retrieving the list of commands and checking whether the menu is empty.
 *
 * <p>The commands are stored as a map, where the keys are command names and the values are
 * {@link BotCommand} instances containing the command name and its associated text.
 *
 * @author Ivan Galushko
 * @see BotCommand
 */
public final class BotMenuImpl implements BotMenu {

  private final Map<String, BotCommand> commands;

  /**
   * Constructs a BotMenuImpl with the specified commands.
   *
   * <p>If the provided commands map is null, an empty map is used to initialize the menu.
   * Otherwise, the map is converted into a map of BotCommand instances.
   *
   * @param commands The map of command names and corresponding CommandCls instances, nullable
   */
  public BotMenuImpl(@Nullable Map<String, CommandCls> commands) {
    if (commands == null) {
      this.commands = Map.of();
    } else {
      this.commands = commands.entrySet().stream()
          .collect(Collectors.toMap(Entry::getKey,
              it -> new BotCommand(it.getValue().getCommand(), it.getValue().getText())));
    }
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
