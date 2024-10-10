package io.github.drednote.telegram.session;

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
import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.utils.Assert;
import java.time.Duration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Implementation of a priority pre-update filter for rate-limiting user requests.
 *
 * <p>This filter uses a caching mechanism to manage rate limits for individual users. It tracks
 * the rate of incoming requests per user, and if the rate limit is exceeded, it returns false,
 * otherwise true.
 *
 * @author Ivan Galushko
 */
public class UserRateLimitRequestFilter {

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
     * Pre-filters the incoming Telegram update userId to rate-limit user requests.
     *
     * <p>This method checks the rate of incoming requests per user using a caching mechanism.
     * If the rate limit is exceeded, it returns false, otherwise true.
     *
     * @param userId The incoming Telegram update userId to be pre-filtered, not null
     * @return if the rate limit is exceeded, it returns false, otherwise true.
     */
    public boolean filter(@NonNull Long userId) {
        Assert.notNull(userId, "userId");

        if (filterProperties.getUserRateLimit() > 0) {
            LocalBucket bucket = getBucket(userId);
            return bucket.tryConsume(1);
        }
        return true;
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
