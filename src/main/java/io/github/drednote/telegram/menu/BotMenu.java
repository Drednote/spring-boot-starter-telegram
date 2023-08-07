package io.github.drednote.telegram.menu;

import java.util.List;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public interface BotMenu {

  List<BotCommand> getCommands();

  boolean isEmpty();

}
