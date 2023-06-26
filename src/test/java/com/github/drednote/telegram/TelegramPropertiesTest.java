package com.github.drednote.telegram;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.drednote.telegram.session.FixedBackoff;
import com.github.drednote.telegram.session.SessionProperties;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.DefaultBotOptions.ProxyType;

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