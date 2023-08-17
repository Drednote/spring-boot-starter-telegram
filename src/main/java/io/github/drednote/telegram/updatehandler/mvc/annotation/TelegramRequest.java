package io.github.drednote.telegram.updatehandler.mvc.annotation;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TelegramRequest {

  /**
   * @see TelegramRequest#pattern
   */
  @AliasFor("pattern")
  String[] value() default {};

  @AliasFor("value")
  String[] pattern() default {};

  RequestType[] requestType() default {};

  /**
   * applied only if {@link #requestType()} = {@link RequestType#MESSAGE} or null
   */
  MessageType[] messageType() default {};

  /**
   * This parameter is responsible for how message types will be mapped. If it is set to false, then
   * the method annotated with {@link TelegramRequest} will accept all message types that are
   * specified in the {@link #messageType()} method. If true, then the method will only accept
   * messages that have all the types listed in the {@link #messageType()} method.
   * <p>
   * In simple words: <b>false = any match, true = all match</b>
   *
   * @apiNote not using if {{@link #messageType()}} is empty
   */
  boolean exclusiveMessageType() default false;
}
