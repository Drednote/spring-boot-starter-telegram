package com.github.drednote.telegram.utils.lock;

public interface ReadWriteKeyLock<K> {

  KeyLock<K> read();

  KeyLock<K> write();

}