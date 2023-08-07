package io.github.drednote.telegram.utils.lock;

public interface ReadWriteKeyLock<K> {

  KeyLock<K> readLock();

  KeyLock<K> writeLock();

}
