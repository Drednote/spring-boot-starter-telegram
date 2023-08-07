package io.github.drednote.telegram.session;

import java.lang.reflect.InvocationTargetException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.DefaultBotOptions.ProxyType;
import org.telegram.telegrambots.meta.generics.BackOff;
import org.telegram.telegrambots.updatesreceivers.ExponentialBackOff;

@Getter
@Setter
@Configuration
@ConfigurationProperties("drednote.telegram-bot.session")
public class SessionProperties {

  private int updateLimit = 100;
  private int updateTimeout = 50;
  private int updateHandlerThreadCount = 1;
  /**
   * how often the user can perform requests. 0 = no rules
   */
  private long userConcurrency = 0L;
  private ChronoUnit userConcurrencyUnit = ChronoUnit.SECONDS;
  private List<String> allowedUpdates = new ArrayList<>();
  /**
   * @apiNote type WebHooks don't implement yet
   */
  private UpdateStrategy updateStrategy = UpdateStrategy.LONG_POLLING;
  private Class<? extends BackOff> backOffStrategy = ExponentialBackOff.class;
  private ProxyType proxyType = ProxyType.NO_PROXY;
  private String proxyHost;
  private int proxyPort;

  public DefaultBotOptions toBotOptions() {
    DefaultBotOptions defaultBotOptions = new DefaultBotOptions();
    defaultBotOptions.setAllowedUpdates(this.getAllowedUpdates());
    defaultBotOptions.setGetUpdatesLimit(this.getUpdateLimit());
    defaultBotOptions.setGetUpdatesTimeout(this.getUpdateTimeout());
    defaultBotOptions.setMaxThreads(this.getUpdateHandlerThreadCount());
    defaultBotOptions.setProxyType(this.getProxyType());
    defaultBotOptions.setProxyHost(this.getProxyHost());
    defaultBotOptions.setProxyPort(this.getProxyPort());

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
    LONG_POLLING, WEBHOOKS
  }
}
