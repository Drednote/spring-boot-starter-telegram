package io.github.drednote.telegram.menu;

import io.github.drednote.telegram.menu.MenuProperties.CommandCls;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Interface representing a bot menu containing a list of bot commands that can be sent to Telegram
 * API with {@link SetMyCommands}
 *
 * <p>Implementations of this interface can provide custom logic for populating the menu
 * with commands and determining its emptiness.
 *
 * @author Ivan Galushko
 * @see BotCommand
 * @see SetMyCommands
 */
public interface BotMenu {

  /**
   * Send commands containing in this class to telegram
   *
   * @param absSender sender
   * @throws TelegramApiException in case of error during sending
   */
  void updateMenu(AbsSender absSender) throws TelegramApiException;

  /**
   * Retrieves the list of bot commands contained in the menu.
   *
   * @return The list of bot commands in the menu
   */
  List<CommandCls> getCommands();

  /**
   * Checks whether the there are no commands to send to Telegram
   *
   * @return {@code true} if the menu contains no commands, {@code false} otherwise
   */
  boolean isEmpty();

}
