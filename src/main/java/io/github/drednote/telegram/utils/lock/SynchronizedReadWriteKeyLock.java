package io.github.drednote.telegram.utils.lock;

import io.github.drednote.telegram.utils.Assert;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SynchronizedReadWriteKeyLock<K> implements ReadWriteKeyLock<K> {

  private final KeyLock<K> read;
  private final KeyLock<K> write;

  public SynchronizedReadWriteKeyLock() {
    Map<K, Queue<Long>> pool = new ConcurrentHashMap<>();

    this.write = new WriteKeyLock<>(pool);
    this.read = new ReadKeyLock<>(pool);
  }

  @Override
  public KeyLock<K> readLock() {
    return read;
  }

  @Override
  public KeyLock<K> writeLock() {
    return write;
  }

  private record WriteKeyLock<K>(Map<K, Queue<Long>> pool) implements KeyLock<K> {

    @Override
    @SuppressWarnings("Duplicates")
    public void lock(K id, long timeout) throws TimeoutException {
      Assert.notNull(id, "id");
      Queue<Long> queue = pool.computeIfAbsent(id, key -> new LinkedList<>());
      synchronized (queue) {
        long threadId = Thread.currentThread().getId();
        log.trace("Try lock {}, thread id = {}", id, threadId);
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
      Assert.notNull(id, "id");
      Queue<Long> queue = pool.get(id);
      if (queue == null) {
        throw new IllegalStateException("Call 'lock' before calling 'unlock'");
      }
      synchronized (queue) {
        long threadId = Thread.currentThread().getId();
        log.trace("Unlock {}, thread id = {}", id, threadId);
        queue.remove(threadId);
        if (queue.isEmpty()) {
          log.trace("Clear {}, thread id = {}", id, threadId);
          pool.remove(id);
        }
        queue.notifyAll();
      }
    }
  }

  private record ReadKeyLock<K>(Map<K, Queue<Long>> pool) implements KeyLock<K> {

    @SuppressWarnings("Duplicates")
    public void lock(K id, long timeout) throws TimeoutException {
      Assert.notNull(id, "id");
      Queue<Long> queue = pool.get(id);
      if (queue != null) {
        synchronized (queue) {
          LocalDateTime dateTimeout = LocalDateTime.now().plus(timeout, ChronoUnit.MILLIS);
          while (pool.containsKey(id)) {
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
