package io.github.drednote.telegram;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.session.SessionProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TelegramProperties.class)
class TelegramPropertiesTest {

  @Autowired
  private TelegramProperties properties;

  @Test
  void shouldHaveCorrectProperties() {
    assertThat(properties.getName()).isEqualTo("simple");
    assertThat(properties.getToken()).isEqualTo("abc");
    SessionProperties session = properties.getSession();
    assertThat(session).isNotNull();
  }
}