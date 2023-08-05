package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.request.ExtendedBotRequest;
import com.github.drednote.telegram.session.SessionProperties;
import com.github.drednote.telegram.updatehandler.response.TooManyRequestsHandlerResponse;
import com.github.drednote.telegram.utils.lock.ReadWriteKeyLock;
import com.github.drednote.telegram.utils.lock.SynchronizedReadWriteKeyLock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

public non-sealed class ConcurrentUserRequestFilter implements PriorityUpdateFilter {

  private final SessionProperties sessionProperties;
  private final Map<Long, Instant> pool = new ConcurrentHashMap<>();
  private final ReadWriteKeyLock<Long> keyLock = new SynchronizedReadWriteKeyLock<>();
  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  public ConcurrentUserRequestFilter(SessionProperties sessionProperties) {
    this.sessionProperties = sessionProperties;
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    executor.scheduleWithFixedDelay(new Cleaner(60_000, sessionProperties, lock, pool),
        30, 30, TimeUnit.SECONDS);
  }

  @Override
  public void preFilter(@NonNull ExtendedBotRequest request) {
    Long chatId = request.getChatId();
    ChronoUnit unit = sessionProperties.getUserConcurrencyUnit();
    long duration = sessionProperties.getUserConcurrency();
    if (duration > 0) {
      try {
        lock.readLock().lock();
        keyLock.writeLock().lock(chatId);
        Instant lastCall = pool.get(chatId);
        if (lastCall != null && unit.between(lastCall, Instant.now()) < duration) {
          request.setResponse(TooManyRequestsHandlerResponse.INSTANCE);
        } else {
          pool.put(chatId, Instant.now());
        }
      } finally {
        keyLock.writeLock().unlock(chatId);
        lock.readLock().unlock();
      }
    }
  }

  @Override
  public final int getPreOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 102;
  }

  @RequiredArgsConstructor
  static class Cleaner implements Runnable {

    /**
     * in ms
     */
    private final long staleFactor;
    private final SessionProperties sessionProperties;
    private final ReadWriteLock lock;
    private final Map<Long, Instant> pool;

    @Override
    public void run() {
      if (pool.isEmpty()) {
        return;
      }

      Instant now = Instant.now();
      long userConcurrency = sessionProperties.getUserConcurrency();
      ChronoUnit unit = sessionProperties.getUserConcurrencyUnit();

      Duration base = unit.getDuration().plus(userConcurrency, unit);
      Duration staleDuration = base.plus(Duration.ofMillis(staleFactor));

      lock.writeLock().lock();
      Map<Long, Instant> newPool = new HashMap<>();
      for (Entry<Long, Instant> entry : pool.entrySet()) {
        Instant creationTime = entry.getValue();
        if (Duration.between(creationTime, now).compareTo(staleDuration) < 0) {
          newPool.put(entry.getKey(), creationTime);
        }
      }
      pool.clear();
      pool.putAll(newPool);
      lock.writeLock().unlock();
    }
  }
}
