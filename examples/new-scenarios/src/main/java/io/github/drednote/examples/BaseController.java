package io.github.drednote.examples;

import io.github.drednote.telegram.core.annotation.TelegramCommand;
import io.github.drednote.telegram.core.annotation.TelegramController;
import io.github.drednote.telegram.core.annotation.TelegramPatternVariable;
import io.github.drednote.telegram.core.request.UpdateRequest;
import org.telegram.telegrambots.meta.api.objects.User;

@TelegramController
public class BaseController
{
  @TelegramCommand("/start{command:.*}")
  public String onStart( @TelegramPatternVariable("command") String command, User user, UpdateRequest request ) {
    return "Hello!";
  }
}
