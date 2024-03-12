package io.github.drednote.telegram.utils.lock;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public final class SynchronizedReadWriteKeyLock<K> implements ReadWriteKeyLock<K> {

  private static final String ID_MUST_NOT_BE_NULL = "id must not be null";
  private final KeyLock<K> read;
  private final KeyLock<K> write;
  private final long clearSize;
  private final ReadWriteLock readWriteLock;
  private final Map<K, Queue<Long>> pool;

  /**
   * for testing
   */
  SynchronizedReadWriteKeyLock(
      long clearDelay, TimeUnit clearUnit, long clearSize, Map<K, Queue<Long>> pool
  ) {
    if (clearDelay > 0L) {
      ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
      scheduler.scheduleWithFixedDelay(this::clearStaledKeys, clearDelay, clearDelay, clearUnit);
    }

    this.pool = pool;
    this.readWriteLock = new ReentrantReadWriteLock();
    this.write = new WriteKeyLock<>(this.pool, readWriteLock);
    this.read = new ReadKeyLock<>(this.pool, readWriteLock);
    this.clearSize = clearSize;
  }

  /**
   * @param clearDelay паузы между очисткой кеша. Если значение равно 0 очистка не производится.
   * @param clearUnit  единицы измерения паузы.
   * @param clearSize  минимальный размер кэша при котором очистка производится.
   */
  public SynchronizedReadWriteKeyLock(long clearDelay, TimeUnit clearUnit, long clearSize) {
    this(clearDelay, clearUnit, clearSize, new ConcurrentHashMap<>());
  }

  public SynchronizedReadWriteKeyLock() {
    this(1, TimeUnit.MINUTES, 200);
  }

  @Override
  public KeyLock<K> readLock() {
    return read;
  }

  @Override
  public KeyLock<K> writeLock() {
    return write;
  }

  /**
   * Clear staled keys with queues from the pool.
   */
  public void clearStaledKeys() {
    if (pool.size() > clearSize) {
      readWriteLock.writeLock().lock();
      List<K> keysForRemove = pool.entrySet().stream()
          .filter(entry -> entry.getValue().isEmpty())
          .map(Entry::getKey)
          .toList();
      log.trace("Clear keys: {}", keysForRemove);
      keysForRemove.forEach(pool::remove);
      readWriteLock.writeLock().unlock();
    }
  }

  private record WriteKeyLock<K>(Map<K, Queue<Long>> pool, ReadWriteLock clearPoolLock)
      implements KeyLock<K> {

    @Override
    public void lock(K id, long timeout) throws TimeoutException {
      Assert.notNull(id, ID_MUST_NOT_BE_NULL);

      try {
        clearPoolLock.readLock().lock();

        long threadId = Thread.currentThread().getId();
        log.trace("Try lock {}, thread id = {}", id, threadId);

        doLock(id, timeout, threadId);
      } finally {
        clearPoolLock.readLock().unlock();
      }
    }

    private void doLock(
        K id, long timeout, long threadId
    ) throws TimeoutException {
      Queue<Long> queue = pool.computeIfAbsent(id, key -> new LinkedList<>());
      synchronized (queue) {
        log.trace("Lock {}, thread id = {}", id, threadId);
        LocalDateTime dateTimeout = LocalDateTime.now().plus(timeout, ChronoUnit.MILLIS);
        queue.add(threadId);

        while (!Objects.equals(pool.get(id).peek(), threadId)) {
          log.trace("Wait {}, thread id = {}", id, threadId);
          try {
            queue.wait();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          if (timeout > 0L && LocalDateTime.now().isAfter(dateTimeout)) {
            throw new TimeoutException(String.format("Timeout while waiting to lock %s", id));
          }
        }

        log.trace("Pass lock {}, thread id = {}", id, threadId);
      }
    }

    public void lock(K id) {
      try {
        this.lock(id, 0L);
      } catch (TimeoutException ignore) {
        // will not be
      }
    }

    public void unlock(K id) {
      Assert.notNull(id, ID_MUST_NOT_BE_NULL);

      try {
        clearPoolLock.readLock().lock();
        long threadId = Thread.currentThread().getId();

        Queue<Long> queue = pool.get(id);
        if (queue == null || !queue.contains(threadId)) {
          throw new IllegalStateException(
              "Call 'lock' before calling 'unlock'. key = '%s', thread id = '%s'".formatted(id,
                  Thread.currentThread().getId()));
        }

        doUnlock(id, threadId);
      } finally {
        clearPoolLock.readLock().unlock();
      }
    }

    private void doUnlock(K id, long threadId) {
      Queue<Long> queue = pool.get(id);

      synchronized (queue) {
        log.trace("Unlock {}, thread id = {}", id, threadId);
        queue.remove(threadId);
        queue.notifyAll();
      }
    }
  }

  /**
   * todo add priority like in write lock
   */
  private record ReadKeyLock<K>(Map<K, Queue<Long>> pool, ReadWriteLock clearPoolLock)
      implements KeyLock<K> {

    public void lock(K id, long timeout) throws TimeoutException {
      Assert.notNull(id, ID_MUST_NOT_BE_NULL);

      try {
        clearPoolLock.readLock().lock();
        Queue<Long> queue = pool.get(id);

        if (queue != null && !queue.isEmpty()) {
          doUnlock(id, timeout);
        }
      } finally {
        clearPoolLock.readLock().unlock();
      }
    }

    private void doUnlock(K id, long timeout) throws TimeoutException {
      Queue<Long> queue = pool.get(id);

      synchronized (queue) {
        LocalDateTime dateTimeout = LocalDateTime.now().plus(timeout, ChronoUnit.MILLIS);
        while (!queue.isEmpty()) {
          try {
            queue.wait();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          if (timeout > 0L && LocalDateTime.now().isAfter(dateTimeout)) {
            throw new TimeoutException(String.format("Timeout while waiting to lock %s", id));
          }
        }
      }
    }

    public void lock(K id) {
      try {
        this.lock(id, 0L);
      } catch (TimeoutException ignore) {
        // will not be
      }
    }

    public void unlock(K id) {
      // nothing to do
    }
  }

}
