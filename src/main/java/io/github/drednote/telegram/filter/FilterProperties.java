package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.updatehandler.response.NotHandledTelegramResponse;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("drednote.telegram.filters")
@EnableConfigurationProperties(PermissionProperties.class)
@Getter
@Setter
public class FilterProperties {

  /**
   * Permission filter properties
   *
   * @see AccessPermissionFilter
   */
  private PermissionProperties permission = new PermissionProperties();
  /**
   * How often each user can perform requests to bot. 0 = no rules
   *
   * @see ConcurrentUserRequestFilter
   */
  private long userConcurrency = 0L;
  /**
   * The {@link ChronoUnit} which will be applied to {@link #userConcurrency}
   *
   * @see ConcurrentUserRequestFilter
   */
  private ChronoUnit userConcurrencyUnit = ChronoUnit.SECONDS;
  /**
   * If at the end of update handling and post filtering, the response is null, set
   * {@link NotHandledTelegramResponse} as response
   *
   * @see NotHandledUpdateFilter
   */
  private boolean setDefaultAnswer = true;

}
