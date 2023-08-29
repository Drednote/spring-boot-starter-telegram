package io.github.drednote.telegram.session;

import static org.telegram.telegrambots.Constants.SOCKET_TIMEOUT;

import io.github.drednote.telegram.core.LongPollingBot;
import io.github.drednote.telegram.core.request.RequestType;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.DefaultBotOptions.ProxyType;
import org.telegram.telegrambots.meta.generics.BackOff;
import org.telegram.telegrambots.updatesreceivers.ExponentialBackOff;

/**
 * @see <a href="https://core.telegram.org/bots/api">Telegram API docs</a>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("drednote.telegram.session")
public class SessionProperties {

  /**
   * Limits the number of updates to be retrieved. Values between 1-100 are accepted
   *
   * @apiNote applies only to long polling session
   */
  private int updateLimit = 100;
  /**
   * Timeout in seconds for long polling. Should be positive, short polling (0) should be used for
   * testing purposes only
   *
   * @apiNote applies only to long polling session
   */
  private int updateTimeout = 50;
  /**
   * Max number of threads used for async methods executions (send messages to telegram)
   */
  private int produceMaxThreads = 10;
  /**
   * Max number of threads used for consumption messages from a telegram
   */
  private int consumeMaxThreads = 1;
  /**
   * A JSON-serialized list of the update types you want your bot to receive. For example, specify
   * [“message”, “edited_channel_post”, “callback_query”] to only receive updates of these types.
   * See {@link RequestType} for a complete list of available update types. Specify an empty list to
   * receive all update types except chat_member (default). If not specified, the previous setting
   * will be used
   */
  private List<String> allowedUpdates;
  /**
   * The strategy to receive updates from Telegram API
   *
   * @apiNote type WebHooks not implemented yet
   * @see <a href="https://core.telegram.org/bots/api#getting-updates">Getting updates</a>
   */
  private UpdateStrategy updateStrategy = UpdateStrategy.LONG_POLLING;
  /**
   * Backoff strategy which will be applied if requests to telegram API are failed with errors
   *
   * @apiNote impl of interface {@link BackOff} must have one empty public constructor
   */
  private Class<? extends BackOff> backOffStrategy = ExponentialBackOff.class;
  /**
   * The proxy type for executing requests to telegram API
   */
  private ProxyType proxyType = ProxyType.NO_PROXY;
  /**
   * The proxy host
   */
  private String proxyHost;
  /**
   * The proxy port
   */
  private int proxyPort;

  public DefaultBotOptions toBotOptions() {
    DefaultBotOptions defaultBotOptions = new DefaultBotOptions();
    defaultBotOptions.setAllowedUpdates(this.getAllowedUpdates());
    defaultBotOptions.setGetUpdatesLimit(this.getUpdateLimit());
    defaultBotOptions.setGetUpdatesTimeout(this.getUpdateTimeout());
    defaultBotOptions.setMaxThreads(this.getProduceMaxThreads());
    defaultBotOptions.setProxyType(this.getProxyType());
    defaultBotOptions.setProxyHost(this.getProxyHost());
    defaultBotOptions.setProxyPort(this.getProxyPort());

    if (defaultBotOptions.getRequestConfig() == null) {
      defaultBotOptions.setRequestConfig(
          RequestConfig.copy(RequestConfig.custom().build())
              .setSocketTimeout(SOCKET_TIMEOUT)
              .setConnectTimeout(SOCKET_TIMEOUT)
              .setConnectionRequestTimeout(SOCKET_TIMEOUT).build()
      );
    }

    try {
      Class<? extends BackOff> backOffClazz = this.getBackOffStrategy();
      BackOff backOff = backOffClazz.isAssignableFrom(ExponentialBackOff.class)
          ? new ExponentialBackOff.Builder().build()
          : backOffClazz.getDeclaredConstructor().newInstance();
      defaultBotOptions.setBackOff(backOff);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      throw new BeanCreationException("Cannot initiate BackOff", e);
    }

    return defaultBotOptions;
  }

  public enum UpdateStrategy {
    /**
     * @see LongPollingBot
     * @see LongPollingSession
     */
    LONG_POLLING,
    /**
     * WebHooks not implemented yet
     */
    WEBHOOKS
  }
}
