package io.github.drednote.telegram.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code TelegramExceptionHandler} annotation is annotation that marks a method as a handler
 * for Telegram-related exceptions.
 * <p>
 * This annotation worked in pair with {@link TelegramAdvice}. The class should be marked with
 * {@code TelegramAdvice} and methods of this class, should be marked with
 * {@code TelegramExceptionHandler}
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * @TelegramAdvice
 * public class MyTelegramAdvice {
 *
 *     @TelegramExceptionHandler(RuntimeException.class)
 *     public void handleStartCommand(RuntimeException exception) {
 *         // Handle exception
 *     }
 * }
 * }
 * </pre>
 *
 * <p>This annotation is used to identify methods that serve as exception handlers within a
 * Telegram context. It allows specifying the types of exceptions that the annotated method is
 * capable of handling.
 *
 * @author Ivan Galushko
 * @see TelegramAdvice
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TelegramExceptionHandler {

  /**
   * The types of exceptions that the annotated method can handle
   *
   * @return An array of exception types
   */
  Class<? extends Throwable>[] value() default {};
}
