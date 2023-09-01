package io.github.drednote.telegram.core.annotation;

import io.github.drednote.telegram.core.resolver.TelegramPatternVariableArgumentResolver;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * The {@code TelegramPatternVariable} annotation indicates that a method parameter should be bound
 * to a pattern template variable extracted from the request. This annotation is used in conjunction
 * with the {@link TelegramRequest} annotated handler methods to access specific variables extracted
 * from the pattern of the request mapping.
 * <p>
 * If the method parameter is of type {@link java.util.Map Map&lt;String, String&gt;}, the map will
 * be populated with all pattern variable names and their corresponding values.
 * <p>
 * This annotation serves a similar purpose as {@link PathVariable} in Spring MVC, allowing you to
 * easily access and use pattern variables from the request within your controller methods.
 *
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * @TelegramController
 * public class MyTelegramController {
 *
 *     @TelegramRequest("hello {userId}")
 *     public void handleUserRequest(@TelegramPatternVariable("userId") String userId) {
 *         // Handle user request using the extracted userId
 *     }
 * }
 * }
 * </pre>
 * In the above example, the {@code handleUserRequest} method will be invoked when the request URL
 * pattern matches {@code "hello {userId}"}. The value of the {@code userId} pattern variable will
 * be extracted and bound to the method parameter.
 *
 * @author Ivan Galushko
 * @see TelegramRequest
 * @see TelegramPatternVariableArgumentResolver
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TelegramPatternVariable {

  /**
   * Alias for {@link #name}.
   *
   * @return the name of the pattern variable to bind to
   */
  @AliasFor("name")
  String value() default "";

  /**
   * The name of the pattern variable to bind to.
   *
   * @return the name of the pattern variable to bind to
   */
  @AliasFor("value")
  String name() default "";

  /**
   * Whether the pattern variable is required.
   * <p>Defaults to {@code true}, leading to an exception being thrown if the pattern
   * variable is missing in the incoming request. Switch this to {@code false} if you prefer a
   * {@code null} or Java 8 {@code java.util.Optional}.
   *
   * @return {@code true} if the pattern variable is required, {@code false} otherwise
   */
  boolean required() default true;
}
