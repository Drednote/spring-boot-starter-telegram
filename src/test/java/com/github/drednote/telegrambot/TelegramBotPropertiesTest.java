package com.github.drednote.telegrambot;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.drednote.telegrambot.session.SessionProperties;
import com.github.drednote.telegrambot.session.backoff.FixedBackoff;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.DefaultBotOptions.ProxyType;

@SpringBootTest(classes = TelegramBotProperties.class)
class TelegramBotPropertiesTest {

  @Autowired
  private TelegramBotProperties properties;

  @Test
  void shouldHaveCorrectProperties() {
    assertThat(properties.getName()).isEqualTo("simple");
    assertThat(properties.getToken()).isEqualTo("abc");
    SessionProperties session = properties.getSession();
    assertThat(session).isNotNull();

    DefaultBotOptions expected = new DefaultBotOptions();
    expected.setAllowedUpdates(Collections.emptyList());
    expected.setGetUpdatesLimit(90);
    expected.setGetUpdatesTimeout(40);
    expected.setBackOff(new FixedBackoff());
    expected.setMaxThreads(10);
    expected.setProxyType(ProxyType.SOCKS4);
    expected.setProxyHost("hostProxy");
    expected.setProxyPort(8080);

    assertThat(session.toBotOptions()).usingRecursiveComparison().isEqualTo(expected);
  }
}