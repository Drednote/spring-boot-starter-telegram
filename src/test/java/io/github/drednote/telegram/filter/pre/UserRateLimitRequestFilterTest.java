package io.github.drednote.telegram.filter.pre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.github.drednote.telegram.core.request.DefaultTelegramUpdateRequest;
import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import io.github.drednote.telegram.response.TooManyRequestsTelegramResponse;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class UserRateLimitRequestFilterTest {

  @Test
  void testPreFilterWithOnceAccess() {
    FilterProperties filterProperties = new FilterProperties();
    filterProperties.setUserRateLimitUnit(ChronoUnit.MINUTES);
    filterProperties.setUserRateLimit(10L);

    UserRateLimitRequestFilter filter = new UserRateLimitRequestFilter(filterProperties);

    DefaultTelegramUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createEmpty());

    filter.preFilter(request);

    assertNull(request.getResponse());
  }

  @Test
  void testPreFilterWithInvalidDuration() throws InterruptedException {
    FilterProperties filterProperties = new FilterProperties();
    filterProperties.setUserRateLimitUnit(ChronoUnit.SECONDS);
    filterProperties.setUserRateLimit(100L);

    UserRateLimitRequestFilter filter = new UserRateLimitRequestFilter(filterProperties);

    DefaultTelegramUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createEmpty());

    filter.preFilter(request);
    Thread.sleep(50);
    filter.preFilter(request);

    assertSame(TooManyRequestsTelegramResponse.INSTANCE, request.getResponse());
  }

  @Test
  void testPreFilterWithValidDuration() throws InterruptedException {
    FilterProperties filterProperties = new FilterProperties();
    filterProperties.setUserRateLimitUnit(ChronoUnit.MILLIS);
    filterProperties.setUserRateLimit(10L);

    UserRateLimitRequestFilter filter = new UserRateLimitRequestFilter(filterProperties);

    DefaultTelegramUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createEmpty());

    filter.preFilter(request);
    Thread.sleep(50);
    filter.preFilter(request);

    assertNull(request.getResponse());
  }

  @Test
  void shouldCleanStaledObjectsCorrect() throws InterruptedException {
    FilterProperties filterProperties = new FilterProperties();
    filterProperties.setUserRateLimitUnit(ChronoUnit.MILLIS);
    filterProperties.setUserRateLimit(10L);
    filterProperties.setUserRateLimitCacheExpireUnit(ChronoUnit.MILLIS);
    filterProperties.setUserRateLimitCacheExpire(20L);

    AtomicLong key = new AtomicLong(1L);
    UserRateLimitRequestFilter filter = new UserRateLimitRequestFilter(filterProperties);
    filter.setCacheEvictionCallback((key1, value, cause) -> key.getAndIncrement());

    DefaultTelegramUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createEmpty());

    filter.preFilter(request);
    Thread.sleep(50);
    filter.preFilter(request);
    assertThat(key.get()).isEqualTo(2L);
  }
}