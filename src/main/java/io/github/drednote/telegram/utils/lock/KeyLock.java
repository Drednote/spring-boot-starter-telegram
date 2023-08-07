package io.github.drednote.telegram.utils.lock;

import java.util.concurrent.TimeoutException;

public interface KeyLock<K> {

  void lock(K id, long timeout) throws TimeoutException;

  void lock(K id);

  void unlock(K id);
}
