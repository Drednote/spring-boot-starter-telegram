package io.github.drednote.telegram.filter.pre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.session.UserRateLimitRequestFilter;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class UserRateLimitRequestFilterTest {

  @Test
  void testFilterWithOnceAccess() {
    FilterProperties filterProperties = new FilterProperties();
    filterProperties.setUserRateLimitUnit(ChronoUnit.MINUTES);
    filterProperties.setUserRateLimit(10L);

    UserRateLimitRequestFilter filter = new UserRateLimitRequestFilter(filterProperties);

    DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createEmpty());

    filter.filter(request.getUser().getId());

    assertNull(request.getResponse());
  }

  @Test
  void testFilterWithInvalidDuration() throws InterruptedException {
    FilterProperties filterProperties = new FilterProperties();
    filterProperties.setUserRateLimitUnit(ChronoUnit.SECONDS);
    filterProperties.setUserRateLimit(100L);

    UserRateLimitRequestFilter filter = new UserRateLimitRequestFilter(filterProperties);

    DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createEmpty());

    assertThat(filter.filter(request.getUser().getId())).isTrue();
    Thread.sleep(50);
    assertThat(filter.filter(request.getUser().getId())).isFalse();
  }

  @Test
  void testFilterWithValidDuration() throws InterruptedException {
    FilterProperties filterProperties = new FilterProperties();
    filterProperties.setUserRateLimitUnit(ChronoUnit.MILLIS);
    filterProperties.setUserRateLimit(10L);

    UserRateLimitRequestFilter filter = new UserRateLimitRequestFilter(filterProperties);

    DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createEmpty());

    filter.filter(request.getUser().getId());
    Thread.sleep(50);
    filter.filter(request.getUser().getId());

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

    DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createEmpty());

    filter.filter(request.getUser().getId());
    Thread.sleep(50);
    filter.filter(request.getUser().getId());
    assertThat(key.get()).isEqualTo(2L);
  }
}