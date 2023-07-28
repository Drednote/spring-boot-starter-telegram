package com.github.drednote.telegram.utils.lock;

import com.github.drednote.telegram.utils.Assert;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SynchronizedReadWriteKeyLock<K> implements ReadWriteKeyLock<K> {

  private final KeyLock<K> read;
  private final KeyLock<K> write;

  public SynchronizedReadWriteKeyLock() {
    Map<K, Queue<Long>> pool = new HashMap<>();

    this.write = new WriteKeyLock<>(pool);
    this.read = new ReadKeyLock<>(pool);
  }

  @Override
  public KeyLock<K> read() {
    return read;
  }

  @Override
  public KeyLock<K> write() {
    return write;
  }

  private record WriteKeyLock<K>(Map<K, Queue<Long>> pool) implements KeyLock<K> {

    @Override
    @SuppressWarnings("Duplicates")
    public void lock(K id, long timeout) throws InterruptedException, TimeoutException {
      Assert.notNull(id, "id");
      long threadId = Thread.currentThread().getId();
      synchronized (pool) {
        log.trace("Try lock {}, thread id = {}", id, threadId);
        LocalDateTime dateTimeout = LocalDateTime.now().plus(timeout, ChronoUnit.MILLIS);
        pool.computeIfAbsent(id, key -> new LinkedList<>()).add(threadId);
        while (!Objects.equals(pool.get(id).peek(), threadId)) {
          log.trace("Wait {}, thread id = {}", id, threadId);
          pool.wait();
          if (timeout > 0L && LocalDateTime.now().isAfter(dateTimeout)) {
            throw new TimeoutException(String.format("Timeout while waiting to lock %s", id));
          }
        }
        log.trace("Pass lock {}, thread id = {}", id, threadId);
      }
    }

    public void lock(K id) throws InterruptedException {
      try {
        this.lock(id, 0L);
      } catch (TimeoutException ignore) {
        // will not be
      }
    }

    public void unlock(K id) {
      Assert.notNull(id, "id");
      long threadId = Thread.currentThread().getId();
      synchronized (pool) {
        log.trace("Unlock {}, thread id = {}", id, threadId);
        Queue<Long> queue = pool.get(id);
        if (queue != null) {
          queue.remove(threadId);
          if (queue.isEmpty()) {
            log.trace("Clear {}, thread id = {}", id, threadId);
            pool.remove(id);
          }
        } else {
          // should it to be thrown?
          throw new IllegalStateException("Call 'lock' before calling 'unlock'");
        }
        pool.notifyAll();
      }
    }
  }

  private record ReadKeyLock<K>(Map<K, Queue<Long>> pool) implements KeyLock<K> {

    @SuppressWarnings("Duplicates")
    public void lock(K id, long timeout) throws InterruptedException, TimeoutException {
      Assert.notNull(id, "id");
      if (pool.containsKey(id)) {
        synchronized (pool) {
          LocalDateTime dateTimeout = LocalDateTime.now().plus(timeout, ChronoUnit.MILLIS);
          while (pool.containsKey(id)) {
            pool.wait(timeout);
            if (timeout > 0L && LocalDateTime.now().isAfter(dateTimeout)) {
              throw new TimeoutException(String.format("Timeout while waiting to lock %s", id));
            }
          }
        }
      }
    }

    public void lock(K id) throws InterruptedException {
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
