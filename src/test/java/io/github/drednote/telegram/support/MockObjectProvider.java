package io.github.drednote.telegram.support;

import io.github.drednote.telegram.utils.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public class MockObjectProvider<T> implements ObjectProvider<T> {

  @Nullable
  private final T object;

  @NonNull
  @Override
  public T getObject(Object... args) throws BeansException {
    Assert.notNull(object, "Object");
    return object;
  }

  @Nullable
  @Override
  public T getIfAvailable() throws BeansException {
    return object;
  }

  @Nullable
  @Override
  public T getIfUnique() throws BeansException {
    return object;
  }

  @Override
  public T getObject() throws BeansException {
    Assert.notNull(object, "Object");
    return object;
  }
}
