package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.filter.post.NotHandledUpdateFilter;
import io.github.drednote.telegram.filter.pre.AccessPermissionFilter;
import io.github.drednote.telegram.session.UserRateLimitRequestFilter;
import io.github.drednote.telegram.response.NotHandledTelegramResponse;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

/**
 * Properties for filters
 *
 * @author Ivan Galushko
 */
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
  @NonNull
  private PermissionProperties permission = new PermissionProperties();
  /**
   * How often each user can perform requests to bot. 0 = no rules
   *
   * @see UserRateLimitRequestFilter
   */
  @NonNull
  private long userRateLimit = 0L;
  /**
   * The {@link ChronoUnit} which will be applied to {@link #userRateLimit}
   *
   * @see UserRateLimitRequestFilter
   */
  @NonNull
  private ChronoUnit userRateLimitUnit = ChronoUnit.SECONDS;
  /**
   * How long cache with rate limit bucket will not expire. This parameter needed just for delete
   * staled buckets to free up memory
   *
   * @apiNote cache duration cannot be less than {@link #userRateLimit}, so if you specify value
   * less than {@code #userRateLimit}, then cache will be expired after value of
   * {@code #userRateLimit}
   * @see UserRateLimitRequestFilter
   */
  @NonNull
  private long userRateLimitCacheExpire = 1L;
  /**
   * The {@link ChronoUnit} which will be applied to {@link #userRateLimitCacheExpire}
   *
   * @see UserRateLimitRequestFilter
   */
  @NonNull
  private ChronoUnit userRateLimitCacheExpireUnit = ChronoUnit.HOURS;
  /**
   * If at the end of update handling and post filtering, the response is null, set
   * {@link NotHandledTelegramResponse} as response
   *
   * @see NotHandledUpdateFilter
   */
  @NonNull
  private boolean setDefaultAnswer = true;

}
