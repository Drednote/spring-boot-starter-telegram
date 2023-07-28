package com.github.drednote.telegram.utils.lock;

import java.util.concurrent.TimeoutException;

public interface KeyLock<K> {

  void lock(K id, long timeout) throws InterruptedException, TimeoutException;

  void lock(K id) throws InterruptedException;

  void unlock(K id);
}
