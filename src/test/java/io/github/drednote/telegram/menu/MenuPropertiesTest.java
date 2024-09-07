package io.github.drednote.telegram.menu;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.github.drednote.telegram.menu.MenuProperties.CommandCls;
import io.github.drednote.telegram.menu.MenuProperties.ScopeCommand;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.lang.Nullable;

class MenuPropertiesTest {

    @ParameterizedTest
    @CsvSource(value = {
        "a-d, validText",
        "aD, validText",
        "df923, validText",
        "as/s, validText",
        ", validText",
        "ad, ",
        "null, validText",
        "validText, null",
    }, nullValues = "null")
    void shouldThrowExceptionIfBaseFieldsInvalid(String command, String text) {
        CommandCls commandCls = new CommandCls();
        commandCls.setCommand(command);
        commandCls.setText(text);
        assertThatThrownBy(commandCls::validate).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({
        "/ad, validText",
        "ad, validText",
    })
    void shouldNotThrowExceptionIfBaseFieldsValid(String command, String text) {
        CommandCls commandCls = new CommandCls();
        commandCls.setCommand(command);
        commandCls.setText(text);
        assertDoesNotThrow(commandCls::validate);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "CHAT_MEMBER, 1, null",
        "CHAT_MEMBER, null, 1",
        "CHAT_MEMBER, 0, 0",
        "CHAT_ADMINISTRATORS, 1, null",
        "CHAT, 1, null",
    }, nullValues = "null")
    void shouldThrowExceptionIfAdditionalFieldsInvalid(
        ScopeCommand scope, @Nullable Long userId, @Nullable Long chatId) {
        MenuProperties.CommandCls commandCls = new CommandCls();
        commandCls.setCommand("/command");
        commandCls.setText("Text");
        commandCls.setScopes(Set.of(scope));
        commandCls.setUserIds(userId != null ? Set.of(userId) : Set.of());
        commandCls.setChatIds(chatId != null ? Set.of(chatId) : Set.of());

        assertThatThrownBy(commandCls::validate).isInstanceOf(IllegalArgumentException.class);
    }
}