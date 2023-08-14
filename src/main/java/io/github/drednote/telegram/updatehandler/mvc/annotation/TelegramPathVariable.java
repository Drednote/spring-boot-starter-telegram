package io.github.drednote.telegram.updatehandler.mvc.annotation;

import io.github.drednote.telegram.core.resolver.PathVariableArgumentResolver;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * The Annotation which indicates that a method parameter should be bound to a URI template
 * variable. Supported for {@link TelegramRequest} annotated handler methods.
 *
 * <p>If the method parameter is {@link java.util.Map Map&lt;String, String&gt;}
 * then the map is populated with all path variable names and values.
 *
 * <p>
 * Works like {@link PathVariable}
 *
 * @author Galushko Ivan
 * @see TelegramRequest
 * @see PathVariableArgumentResolver
 * @since 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TelegramPathVariable {

  /**
   * Alias for {@link #name}.
   */
  @AliasFor("name")
  String value() default "";

  /**
   * The name of the path variable to bind to.
   *
   * @since 4.3.3
   */
  @AliasFor("value")
  String name() default "";

  /**
   * Whether the path variable is required.
   * <p>Defaults to {@code true}, leading to an exception being thrown if the path
   * variable is missing in the incoming request. Switch this to {@code false} if you prefer a
   * {@code null} or Java 8 {@code java.util.Optional}.
   */
  boolean required() default true;
}
