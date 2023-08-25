package io.github.drednote.telegram.filter.pre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import io.github.drednote.telegram.core.request.DefaultTelegramUpdateRequest;
import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.filter.pre.ConcurrentUserRequestFilter.Cleaner;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import io.github.drednote.telegram.updatehandler.response.TooManyRequestsTelegramResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ConcurrentUserRequestFilterTest {

  @Test
  void testPreFilterWithOnceAccess() {
    FilterProperties sessionProperties = Mockito.mock(FilterProperties.class);
    when(sessionProperties.getUserConcurrencyUnit()).thenReturn(ChronoUnit.MINUTES);
    when(sessionProperties.getUserConcurrency()).thenReturn(10L);

    ConcurrentUserRequestFilter filter = new ConcurrentUserRequestFilter(sessionProperties);

    DefaultTelegramUpdateRequest request = UpdateRequestUtils.createMockRequest(UpdateUtils.createEmpty());

    filter.preFilter(request);

    assertNull(request.getResponse());
  }

  @Test
  void testPreFilterWithInvalidDuration() throws InterruptedException {
    FilterProperties sessionProperties = Mockito.mock(FilterProperties.class);
    when(sessionProperties.getUserConcurrencyUnit()).thenReturn(ChronoUnit.SECONDS);
    when(sessionProperties.getUserConcurrency()).thenReturn(100L);

    ConcurrentUserRequestFilter filter = new ConcurrentUserRequestFilter(sessionProperties);

    DefaultTelegramUpdateRequest request = UpdateRequestUtils.createMockRequest(UpdateUtils.createEmpty());

    filter.preFilter(request);
    Thread.sleep(50);
    filter.preFilter(request);

    assertSame(TooManyRequestsTelegramResponse.INSTANCE, request.getResponse());
  }

  @Test
  void testPreFilterWithValidDuration() throws InterruptedException {
    FilterProperties sessionProperties = Mockito.mock(FilterProperties.class);
    when(sessionProperties.getUserConcurrencyUnit()).thenReturn(ChronoUnit.MILLIS);
    when(sessionProperties.getUserConcurrency()).thenReturn(10L);

    ConcurrentUserRequestFilter filter = new ConcurrentUserRequestFilter(sessionProperties);

    DefaultTelegramUpdateRequest request = UpdateRequestUtils.createMockRequest(UpdateUtils.createEmpty());

    filter.preFilter(request);
    Thread.sleep(50);
    filter.preFilter(request);

    assertNull(request.getResponse());
  }

  @Test
  void shouldCleanStaledObjectsCorrect() {
    FilterProperties sessionProperties = Mockito.mock(FilterProperties.class);
    when(sessionProperties.getUserConcurrencyUnit()).thenReturn(ChronoUnit.MILLIS);
    when(sessionProperties.getUserConcurrency()).thenReturn(10L);

    Map<Long, Instant> pool = new HashMap<>();
    long key = 1L;
    Instant now = Instant.now().minusMillis(10);
    pool.put(key, now);

    Cleaner cleaner = new Cleaner(10, sessionProperties, new ReentrantReadWriteLock(), pool);
    cleaner.run();
    // should not be cleaned
    assertThat(pool).hasSize(1).hasEntrySatisfying(key, it -> assertSame(it, now));

    pool.put(key, now.minusMillis(11));

    cleaner.run();
    // should be cleaned
    assertThat(pool).isEmpty();
  }
}