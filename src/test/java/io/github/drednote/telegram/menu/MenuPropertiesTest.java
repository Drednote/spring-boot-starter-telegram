package io.github.drednote.telegram.menu;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.drednote.telegram.menu.MenuProperties.CommandCls;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MenuPropertiesTest {

  @ParameterizedTest
  @CsvSource({
      "a-d", "aD", "df923", "as/s"
  })
  void shouldThrowExceptionIfCommandInvalid(String command) {
    CommandCls commandCls = new CommandCls();
    assertThatThrownBy(() -> commandCls.setCommand(command))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @CsvSource({
      "/ad", "ad"
  })
  void shouldNotThrowExceptionIfCommandValid(String command) {
    CommandCls commandCls = new CommandCls();
    assertThatNoException().isThrownBy(() -> commandCls.setCommand(command));
  }
}