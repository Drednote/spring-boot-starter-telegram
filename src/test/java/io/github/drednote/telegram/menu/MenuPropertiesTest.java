package io.github.drednote.telegram.menu;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.github.drednote.telegram.menu.MenuProperties.CommandCls;
import io.github.drednote.telegram.menu.MenuProperties.ScopeCommand;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
  void shouldThrowExceptionIfAdditionalFieldsInvalid(ScopeCommand scope, Long userId, Long chatId) {
    MenuProperties.CommandCls commandCls = new CommandCls();
    commandCls.setCommand("/command");
    commandCls.setText("Text");
    commandCls.setScope(scope);
    commandCls.setUserId(userId);
    commandCls.setChatId(chatId);

    assertThatThrownBy(commandCls::validate).isInstanceOf(IllegalArgumentException.class);
  }
}