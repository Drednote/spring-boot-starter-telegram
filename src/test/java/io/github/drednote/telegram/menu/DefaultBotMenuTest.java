package io.github.drednote.telegram.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import io.github.drednote.telegram.menu.MenuProperties.CommandCls;
import io.github.drednote.telegram.menu.MenuProperties.ScopeCommand;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllGroupChats;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

class DefaultBotMenuTest {

  @Nullable
  private DefaultBotMenu defaultBotMenu;
  AbsSender absSender;

  @BeforeEach
  void setUp() {
    absSender = Mockito.mock(AbsSender.class);
  }

  @Test
  void shouldCorrectCallAbsSenderIfBaseScope() throws TelegramApiException {
    CommandCls command = new CommandCls();
    command.setCommand("/command");
    command.setText("text");
    command.setLanguageCode("en");

    CommandCls command2 = new CommandCls();
    command2.setCommand("/command");
    command2.setText("text");
    command2.setScope(ScopeCommand.ALL_GROUP_CHATS);

    CommandCls command3 = new CommandCls();
    command3.setCommand("/commandd");
    command3.setText("text2");
    command3.setScope(ScopeCommand.ALL_GROUP_CHATS);

    defaultBotMenu = new DefaultBotMenu(Map.of("f", command, "s", command2, "t", command3));

    assertThat(defaultBotMenu.getCommands()).containsOnly(command, command2, command3);
    assertThat(defaultBotMenu.isEmpty()).isFalse();

    defaultBotMenu.updateMenu(absSender);

    verify(absSender).execute(createSetMyCommands(
        List.of(new BotCommand("/command", "text")),
        new BotCommandScopeDefault(), "en")
    );
    verify(absSender).execute(createSetMyCommands(
        List.of(
            new BotCommand("/command", "text"),
            new BotCommand("/commandd", "text2")
        ),
        new BotCommandScopeAllGroupChats(), null)
    );
  }

  @Test
  void shouldCorrectCallAbsSenderIfCustomScope() throws TelegramApiException {
    CommandCls command = new CommandCls();
    command.setCommand("/command");
    command.setText("text");
    command.setLanguageCode("en");
    command.setScope(ScopeCommand.CHAT_MEMBER);
    command.setChatId(1L);
    command.setUserId(1L);

    CommandCls command2 = new CommandCls();
    command2.setCommand("/command");
    command2.setText("text");
    command2.setScope(ScopeCommand.CHAT_ADMINISTRATORS);
    command2.setChatId(1L);

    CommandCls command3 = new CommandCls();
    command3.setCommand("/commandd");
    command3.setText("text");
    command3.setLanguageCode("en");
    command3.setScope(ScopeCommand.CHAT_MEMBER);
    command3.setChatId(2L);
    command3.setUserId(2L);

    defaultBotMenu = new DefaultBotMenu(Map.of("f", command, "s", command2, "t", command3));

    assertThat(defaultBotMenu.getCommands()).containsOnly(command, command2, command3);
    assertThat(defaultBotMenu.isEmpty()).isFalse();

    defaultBotMenu.updateMenu(absSender);

    verify(absSender).execute(createSetMyCommands(
        List.of(new BotCommand("/command", "text")),
        new BotCommandScopeChatMember("1", 1L), "en")
    );
    verify(absSender).execute(createSetMyCommands(
        List.of(new BotCommand("/commandd", "text")),
        new BotCommandScopeChatMember("2", 2L), "en")
    );
    verify(absSender).execute(createSetMyCommands(
        List.of(new BotCommand("/command", "text")),
        new BotCommandScopeChatAdministrators("1"), null)
    );
  }

  @Test
  void shouldDoNothingIfCommandsEmpty() throws TelegramApiException {
    defaultBotMenu = new DefaultBotMenu(Map.of());

    defaultBotMenu.updateMenu(absSender);

    assertThat(defaultBotMenu.isEmpty()).isTrue();
    verifyNoInteractions(absSender);
  }

  public SetMyCommands createSetMyCommands(
      @NonNull List<BotCommand> botCommands, BotCommandScope scope, @Nullable String languageCode
  ) {
    SetMyCommands setMyCommands = new SetMyCommands();
    setMyCommands.setCommands(botCommands);
    setMyCommands.setScope(scope);
    setMyCommands.setLanguageCode(languageCode);
    return setMyCommands;
  }
}