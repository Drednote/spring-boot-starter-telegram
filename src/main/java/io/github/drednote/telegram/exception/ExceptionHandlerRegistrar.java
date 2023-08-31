package io.github.drednote.telegram.exception;

import org.springframework.lang.NonNull;

/**
 * The {@code ExceptionHandlerRegistrar} interface defines a contract for registering exception
 * handler methods associated with a specific bean and its target class.
 *
 * <p>Implementing classes are responsible for handling the registration of exception handler
 * methods in a way that allows the system to efficiently route exceptions to the appropriate
 * handlers
 *
 * @author Ivan Galushko
 * @see ExceptionHandler
 */
public interface ExceptionHandlerRegistrar {

  /**
   * Registers exception handler methods for a specific bean and its target class
   *
   * @param bean        The object containing the handler methods
   * @param targetClass The target class associated with the bean
   */
  void register(@NonNull Object bean, @NonNull Class<?> targetClass);
}
