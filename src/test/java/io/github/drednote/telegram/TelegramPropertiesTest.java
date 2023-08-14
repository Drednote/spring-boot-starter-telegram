package io.github.drednote.telegram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.telegram.telegrambots.Constants.SOCKET_TIMEOUT;

import io.github.drednote.telegram.session.FixedBackoff;
import io.github.drednote.telegram.session.SessionProperties;
import org.apache.http.client.config.RequestConfig;
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
    expected.setAllowedUpdates(null);
    expected.setGetUpdatesLimit(90);
    expected.setGetUpdatesTimeout(40);
    expected.setBackOff(new FixedBackoff());
    expected.setMaxThreads(10);
    expected.setProxyType(ProxyType.SOCKS4);
    expected.setProxyHost("hostProxy");
    expected.setProxyPort(8080);
    expected.setRequestConfig(
        RequestConfig.copy(RequestConfig.custom().build())
            .setSocketTimeout(SOCKET_TIMEOUT)
            .setConnectTimeout(SOCKET_TIMEOUT)
            .setConnectionRequestTimeout(SOCKET_TIMEOUT).build());

    assertThat(session.toBotOptions()).usingRecursiveComparison().isEqualTo(expected);
  }
}