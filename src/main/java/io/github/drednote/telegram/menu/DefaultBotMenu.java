package io.github.drednote.telegram.menu;

import io.github.drednote.telegram.menu.MenuProperties.CommandCls;
import io.github.drednote.telegram.menu.MenuProperties.ScopeCommand;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Implementation of the {@link BotMenu} interface for managing bot commands.
 *
 * <p>This class implements the {@link BotMenu} interface to manage a collection of bot commands.
 * It provides methods for retrieving the list of commands and checking whether the menu is empty.
 *
 * <p>The commands are stored as a map, where the keys are command scope and the values are
 * {@link CommandCls} instances containing the command name and its associated text.
 *
 * @author Ivan Galushko
 * @see BotCommand
 * @see CommandCls
 */
public final class DefaultBotMenu implements BotMenu {

  private final Map<CommandKey, List<CommandCls>> commands = new HashMap<>();

  /**
   * Constructs a DefaultBotMenu with the specified commands.
   *
   * <p>If the provided commands map is null, an empty map is used to initialize the menu.
   * Otherwise, the map is converted into a map of BotCommand instances.
   *
   * @param commands The map of command names and corresponding CommandCls instances, nullable
   */
  public DefaultBotMenu(@Nullable Map<String, CommandCls> commands) {
    if (commands != null) {
      for (CommandCls commandCls : commands.values()) {
        commandCls.validate();
        this.commands
            .computeIfAbsent(new CommandKey(commandCls.getScope(), commandCls.getLanguageCode()),
                key -> new ArrayList<>())
            .add(commandCls);
      }
    }
  }

  @Override
  public boolean isEmpty() {
    return commands.isEmpty();
  }

  @Override
  public void updateMenu(AbsSender absSender) throws TelegramApiException {
    for (var entry : commands.entrySet()) {
      List<CommandCls> commandList = entry.getValue();
      if (commandList.isEmpty()) {
        continue;
      }
      CommandKey commandKey = entry.getKey();
      if (commandKey.scope == ScopeCommand.CHAT_MEMBER) {
        for (CommandCls commandCls : commandList) {
          doExecute(absSender, commandKey, List.of(commandCls));
        }
      } else {
        doExecute(absSender, commandKey, commandList);
      }
    }
  }

  @Override
  public List<CommandCls> getCommands() {
    return commands.values().stream().flatMap(Collection::stream).toList();
  }

  private void doExecute(
      AbsSender absSender, CommandKey commandKey, List<CommandCls> commandList
  ) throws TelegramApiException {
    List<BotCommand> botCommands = commandList.stream()
        .map(commandCls -> new BotCommand(commandCls.getCommand(), commandCls.getText()))
        .toList();

    SetMyCommands setMyCommands = new SetMyCommands();
    setMyCommands.setCommands(botCommands);
    setMyCommands.setScope(commandKey.getScope(commandList.get(0)));
    setMyCommands.setLanguageCode(commandKey.languageCode);

    absSender.execute(setMyCommands);
  }

  private record CommandKey(ScopeCommand scope, @Nullable String languageCode) {

    public BotCommandScope getScope(CommandCls commandCls) {
      return scope.getBotScope(commandCls);
    }
  }
}
