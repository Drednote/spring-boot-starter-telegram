package io.github.drednote.telegram.filter.pre;

import static org.apache.commons.lang3.ObjectUtils.max;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
import io.github.bucket4j.local.SynchronizationStrategy;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.response.TooManyRequestsTelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import java.time.Duration;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Implementation of a priority pre-update filter for rate-limiting user requests.
 *
 * <p>This filter uses a caching mechanism to manage rate limits for individual users. It tracks
 * the rate of incoming requests per user and responds with a
 * {@link TooManyRequestsTelegramResponse} if the rate limit is exceeded.
 *
 * @author Ivan Galushko
 * @see TooManyRequestsTelegramResponse
 */
public class UserRateLimitRequestFilter implements PriorityPreUpdateFilter {

  private final FilterProperties filterProperties;
  private final Cache<Long, LocalBucket> cache;
  private final Duration duration;
  /**
   * Callback that called on remove cached LocalBucket. Using in testing, but you can use this if
   * you needed
   */
  @Nullable
  private RemovalListener<Long, LocalBucket> cacheEvictionCallback;

  /**
   * Constructs a {@code UserRateLimitRequestFilter} with the specified {@link FilterProperties}
   *
   * @param filterProperties The filter properties for rate-limiting user requests.
   * @throws IllegalArgumentException if filterProperties is null.
   */
  public UserRateLimitRequestFilter(FilterProperties filterProperties) {
    Assert.required(filterProperties, "FilterProperties");

    this.filterProperties = filterProperties;
    this.duration = Duration.of(filterProperties.getUserRateLimit(),
        filterProperties.getUserRateLimitUnit());
    this.cache = Caffeine.newBuilder()
        .expireAfterAccess(getCacheLiveDuration())
        .evictionListener(this::onRemoval)
        .build();
  }

  private Duration getCacheLiveDuration() {
    Duration cacheExpireDuration = Duration.of(filterProperties.getUserRateLimitCacheExpire(),
        filterProperties.getUserRateLimitCacheExpireUnit());
    return max(cacheExpireDuration, duration);
  }

  /**
   * Pre-filters the incoming Telegram update request to rate-limit user requests.
   *
   * <p>This method checks the rate of incoming requests per user using a caching mechanism.
   * If the rate limit is exceeded, it sets the {@link TooManyRequestsTelegramResponse} as the
   * response for the update request.
   *
   * @param request The incoming Telegram update request to be pre-filtered, not null
   */
  @Override
  public void preFilter(@NonNull UpdateRequest request) {
    Assert.notNull(request, "UpdateRequest");

    Long chatId = request.getChatId();
    if (filterProperties.getUserRateLimit() > 0) {
      LocalBucket bucket = getBucket(chatId);
      if (!bucket.tryConsume(1)) {
        request.setResponse(TooManyRequestsTelegramResponse.INSTANCE);
      }
    }
  }

  private LocalBucket getBucket(Long chatId) {
    return cache.get(chatId, key ->
        Bucket.builder()
            .addLimit(Bandwidth.classic(1, Refill.intervally(1, this.duration)))
            .withSynchronizationStrategy(SynchronizationStrategy.SYNCHRONIZED)
            .withMillisecondPrecision()
            .build()
    );
  }

  private void onRemoval(@Nullable Long key, @Nullable LocalBucket value, RemovalCause cause) {
    if (cacheEvictionCallback != null) {
      cacheEvictionCallback.onRemoval(key, value, cause);
    }
  }

  @Override
  public final int getPreOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 102;
  }

  /**
   * Sets the cache eviction callback for this filter.
   *
   * <p>Using in testing, but you can use this if you needed
   *
   * @param cacheEvictionCallback The cache eviction callback to be set, not null
   * @throws IllegalArgumentException if cacheEvictionCallback is null
   */
  public void setCacheEvictionCallback(
      RemovalListener<Long, LocalBucket> cacheEvictionCallback
  ) {
    Assert.notNull(cacheEvictionCallback, "RemovalListener");
    this.cacheEvictionCallback = cacheEvictionCallback;
  }
}
