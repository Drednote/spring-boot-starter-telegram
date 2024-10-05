package io.github.drednote.telegram.utils.lock;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SynchronizedReadWriteKeyLockTest {

  ReadWriteKeyLock<UUID> lock = new SynchronizedReadWriteKeyLock<>();

  @Test
  void shouldNotLockOnDifferentKeys() throws InterruptedException {
    var writeLock = lock.writeLock();
    final var key1 = UUID.randomUUID();
    final var key2 = UUID.randomUUID();
    writeLock.lock(key1);
    var anotherThreadWasExecuted = new AtomicBoolean(false);
    try {
      getThread(writeLock, key2, anotherThreadWasExecuted, true).start();
      Thread.sleep(100);
    } finally {
      Assertions.assertTrue(anotherThreadWasExecuted.get());
      writeLock.unlock(key1);
    }
  }


  @Test
  void shouldLockOnSameKeys() throws InterruptedException {
    final var key = UUID.randomUUID();
    var writeLock = lock.writeLock();
    writeLock.lock(key);
    var anotherThreadWasExecuted = new AtomicBoolean(false);
    try {
      getThread(writeLock, key, anotherThreadWasExecuted, true).start();
      Thread.sleep(100);
    } finally {
      Assertions.assertFalse(anotherThreadWasExecuted.get());
      writeLock.unlock(key);
    }
  }

  @Test
  void shouldUnlockOnlyHead() throws InterruptedException {
    final var key = UUID.randomUUID();
    var writeLock = lock.writeLock();
    writeLock.lock(key);
    var first = new AtomicBoolean(false);
    var second = new AtomicBoolean(false);
    try {
      try {
        Thread thread = getThread(writeLock, key, first, false);
        Thread thread2 = getThread(writeLock, key, second, true);
        thread.start();
        Thread.sleep(10);
        thread2.start();
        Thread.sleep(10);
      } finally {
        Assertions.assertFalse(first.get());
        Assertions.assertFalse(second.get());
        writeLock.unlock(key);
      }
      Thread.sleep(10);
    } finally {
      Assertions.assertTrue(first.get());
      Assertions.assertFalse(second.get());
      writeLock.unlock(key);
    }
  }

  @Test
  void name() throws InterruptedException, ExecutionException {
    final var key = UUID.randomUUID();
    var writeLock = lock.writeLock();
    var aBoolean = new AtomicBoolean(false);
    CompletableFuture[] futures = Stream.iterate(0, it -> it + 1)
        .limit(100)
        .map(it -> CompletableFuture.runAsync(() -> {
          try {
            writeLock.lock(key);
            Thread.sleep(10);
            aBoolean.set(true);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          } finally {
            writeLock.unlock(key);
          }
        })).toArray(CompletableFuture[]::new);
    LocalTime start = LocalTime.now();
    CompletableFuture.allOf(futures).get();
    System.out.printf("Executed time = %s", ChronoUnit.MILLIS.between(start, LocalTime.now()));
  }

  @NotNull
  private Thread getThread(
      KeyLock<UUID> lock, UUID key, AtomicBoolean executed, boolean autoUnlock
  ) {
    return new Thread(() -> {
      try {
        lock.lock(key);
        executed.set(true);
      } finally {
        if (autoUnlock) {
          lock.unlock(key);
        }
      }
    });
  }
}