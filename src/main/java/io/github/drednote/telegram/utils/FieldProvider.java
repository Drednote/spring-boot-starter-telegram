package io.github.drednote.telegram.utils;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.util.function.ThrowingConsumer;

public interface FieldProvider<T> {

  @Nullable
  T getField();

  void setField(@Nullable T t);

  static <K> FieldProvider<K> create(K k) {
    return new FieldProviderImpl<>(k);
  }

  static <K> FieldProvider<K> empty() {
    return new FieldProviderImpl<>(null);
  }

  default void ifExists(Consumer<T> consumer) {
    T field = getField();
    if (field != null) {
      consumer.accept(field);
    }
  }

  default void ifExistsWithException(ThrowingConsumer<T> consumer) throws Exception {
    T field = getField();
    if (field != null) {
      consumer.acceptWithException(field);
    }
  }

  @Getter
  @AllArgsConstructor
  final class FieldProviderImpl<T> implements FieldProvider<T> {

    @Setter
    @Nullable
    private T field;
  }
}
