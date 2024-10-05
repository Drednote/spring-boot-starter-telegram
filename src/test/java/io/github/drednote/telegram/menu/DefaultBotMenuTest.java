package io.github.drednote.telegram.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import io.github.drednote.telegram.menu.MenuProperties.CommandCls;
import io.github.drednote.telegram.menu.MenuProperties.ScopeCommand;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllGroupChats;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

class DefaultBotMenuTest {

    @Nullable
    private DefaultBotMenu defaultBotMenu;
    TelegramClient absSender;

    @BeforeEach
    void setUp() {
        absSender = Mockito.mock(TelegramClient.class);
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
        command2.setScopes(Set.of(ScopeCommand.ALL_GROUP_CHATS));

        CommandCls command3 = new CommandCls();
        command3.setCommand("/commandd");
        command3.setText("text2");
        command3.setScopes(Set.of(ScopeCommand.ALL_GROUP_CHATS));

        defaultBotMenu = new DefaultBotMenu(Map.of("f", command, "s", command2, "t", command3));

        assertThat(defaultBotMenu.getCommands().values().stream().flatMap(Collection::stream)).containsOnly(command,
            command2, command3);
        assertThat(defaultBotMenu.isEmpty()).isFalse();

        defaultBotMenu.updateMenu(absSender);

        SetMyCommands setMyCommands = createSetMyCommands(
            List.of(new BotCommand("/command", "text")),
            new BotCommandScopeDefault(), "en");
        SetMyCommands setMyCommands2 = createSetMyCommands(
            List.of(
                new BotCommand("/command", "text"),
                new BotCommand("/commandd", "text2")
            ),
            new BotCommandScopeAllGroupChats(), null);

        var argumentCaptor = ArgumentCaptor.forClass(SetMyCommands.class);
        verify(absSender, times(2)).execute(argumentCaptor.capture());
        List<SetMyCommands> result = argumentCaptor.getAllValues();

        for (SetMyCommands myCommands : result) {
            if (myCommands.getScope() instanceof BotCommandScopeDefault) {
                assertThat(myCommands.getScope()).isEqualTo(setMyCommands.getScope());
                assertThat(myCommands.getLanguageCode()).isEqualTo(setMyCommands.getLanguageCode());
                assertThat(myCommands.getCommands()).containsExactlyInAnyOrderElementsOf(setMyCommands.getCommands());
            } else {
                assertThat(myCommands.getScope()).isEqualTo(setMyCommands2.getScope());
                assertThat(myCommands.getLanguageCode()).isEqualTo(setMyCommands2.getLanguageCode());
                assertThat(myCommands.getCommands()).containsExactlyInAnyOrderElementsOf(setMyCommands2.getCommands());
            }
        }
    }

    @Test
    void shouldCorrectCallAbsSenderIfCustomScope() throws TelegramApiException {
        CommandCls command = new CommandCls();
        command.setCommand("/command");
        command.setText("text");
        command.setLanguageCode("en");
        command.setScopes(Set.of(ScopeCommand.CHAT_MEMBER));
        command.setChatIds(Set.of(1L));
        command.setUserIds(Set.of(1L));

        CommandCls command2 = new CommandCls();
        command2.setCommand("/command");
        command2.setText("text");
        command2.setScopes(Set.of(ScopeCommand.CHAT_ADMINISTRATORS));
        command2.setChatIds(Set.of(1L));

        CommandCls command3 = new CommandCls();
        command3.setCommand("/commandd");
        command3.setText("text");
        command3.setLanguageCode("en");
        command3.setScopes(Set.of(ScopeCommand.CHAT_MEMBER));
        command3.setChatIds(Set.of(2L));
        command3.setUserIds(Set.of(2L));

        defaultBotMenu = new DefaultBotMenu(Map.of("f", command, "s", command2, "t", command3));

        assertThat(defaultBotMenu.getCommands().values().stream().flatMap(Collection::stream)).containsOnly(command,
            command2, command3);
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
        SetMyCommands setMyCommands = new SetMyCommands(botCommands);
        setMyCommands.setScope(scope);
        setMyCommands.setLanguageCode(languageCode);
        return setMyCommands;
    }
}