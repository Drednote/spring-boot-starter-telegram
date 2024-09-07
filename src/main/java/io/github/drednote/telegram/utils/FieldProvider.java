package io.github.drednote.telegram.utils;

import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.function.ThrowingConsumer;

/**
 * The {@code FieldProvider} interface defines a contract for providing access to a single field
 * value of type {@code T}. It allows setting and getting the field value, performing actions on the
 * value if it exists, and creating instances of {@code FieldProvider}.
 *
 * @param <T> The type of the field value
 * @author Ivan Galushko
 */
public interface FieldProvider<T> {

  /**
   * Gets the field value.
   *
   * @return The field value, or {@code null} if it doesn't exist
   */
  @Nullable
  T getField();

  /**
   * Sets the field value.
   *
   * @param t The field value to set
   */
  void setField(@Nullable T t);

  /**
   * Creates a new instance of {@code FieldProvider} with the specified field value.
   *
   * @param <K> The type of the field value
   * @param k   The field value to initialize the provider with
   * @return A new {@code FieldProvider} instance
   */
  static <K> FieldProvider<K> create(@Nullable K k) {
    return new FieldProviderImpl<>(k);
  }

  /**
   * Creates a new instance of {@code FieldProvider} without any initial field value.
   *
   * @param <K> The type of the field value
   * @return An empty {@code FieldProvider} instance
   */
  static <K> FieldProvider<K> empty() {
    return new FieldProviderImpl<>(null);
  }

  /**
   * Performs the specified action on the field value if it exists.
   *
   * @param consumer The action to perform on the field value
   */
  default void ifExists(@NonNull Consumer<T> consumer) {
    Assert.notNull(consumer, "Consumer");
    T field = getField();
    if (field != null) {
      consumer.accept(field);
    }
  }

  /**
   * Performs the specified action on the field value if it exists, allowing checked exceptions.
   *
   * @param consumer The action to perform on the field value
   * @throws Exception If an exception occurs during the action
   */
  default void ifExistsWithException(ThrowingConsumer<T> consumer) throws Exception {
    Assert.notNull(consumer, "ThrowingConsumer");
    T field = getField();
    if (field != null) {
      consumer.acceptWithException(field);
    }
  }

  default boolean isExists() {
    return getField() != null;
  }

  default Optional<T> toOptional() {
    return Optional.ofNullable(getField());
  }

  /**
   * The implementation class {@code FieldProviderImpl} of the {@code FieldProvider} interface,
   * which holds a single field value of type {@code T}.
   *
   * @param <T> The type of the field value
   */
  final class FieldProviderImpl<T> implements FieldProvider<T> {

    /**
     * The field value
     */
    @Nullable
    private T field;

    public FieldProviderImpl(@Nullable T field) {
      this.field = field;
    }

    @Nullable
    public T getField() {
      return this.field;
    }

    public void setField(@Nullable T field) {
      this.field = field;
    }
  }
}
