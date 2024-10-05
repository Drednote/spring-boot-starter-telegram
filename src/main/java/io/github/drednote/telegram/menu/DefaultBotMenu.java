package io.github.drednote.telegram.menu;

import io.github.drednote.telegram.menu.MenuProperties.CommandCls;
import io.github.drednote.telegram.menu.MenuProperties.ScopeCommand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

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
            commands.values().forEach(this::processCommand);
        }
    }

    private void processCommand(CommandCls commandCls) {
        commandCls.validate();

        Set<Long> chatIds = commandCls.getChatIds().isEmpty() ? Collections.singleton(null) : commandCls.getChatIds();
        Set<Long> userIds = commandCls.getUserIds().isEmpty() ? Collections.singleton(null) : commandCls.getUserIds();

        for (ScopeCommand scope : commandCls.getScopes()) {
            for (Long chatId : chatIds) {
                for (Long userId : userIds) {
                    CommandKey key = new CommandKey(scope,
                        scope.isChatId() ? chatId : null,
                        scope.isUserId() ? userId : null,
                        commandCls.getLanguageCode());
                    this.commands.computeIfAbsent(key, k -> new ArrayList<>()).add(commandCls);
                }
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return commands.isEmpty();
    }

    @Override
    public void updateMenu(TelegramClient absSender) throws TelegramApiException {
        for (var entry : commands.entrySet()) {
            List<CommandCls> commandList = entry.getValue();
            if (commandList.isEmpty()) {
                continue;
            }
            CommandKey commandKey = entry.getKey();
            BotCommandScope scope = commandKey.getScope();
            doExecute(absSender, commandKey, commandList, scope);
        }
    }

    @Override
    public Map<CommandKey, List<CommandCls>> getCommands() {
        return commands;
    }

    private void doExecute(
        TelegramClient absSender, CommandKey commandKey, List<CommandCls> commandList, BotCommandScope scope
    ) throws TelegramApiException {
        List<BotCommand> botCommands = commandList.stream()
            .map(commandCls -> new BotCommand(commandCls.getCommand(), commandCls.getText()))
            .toList();

        SetMyCommands setMyCommands = new SetMyCommands(botCommands);
        setMyCommands.setScope(scope);
        setMyCommands.setLanguageCode(commandKey.languageCode);

        absSender.execute(setMyCommands);
    }

    public record CommandKey(
        ScopeCommand scope,
        @Nullable Long chatId,
        @Nullable Long userId,
        @Nullable String languageCode
    ) {

        public BotCommandScope getScope() {
            return scope.getBotScope(this);
        }
    }
}
