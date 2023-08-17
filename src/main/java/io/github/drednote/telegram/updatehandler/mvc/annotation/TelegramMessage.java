package io.github.drednote.telegram.updatehandler.mvc.annotation;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * @see TelegramRequest
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TelegramRequest(requestType = RequestType.MESSAGE)
public @interface TelegramMessage {

  /**
   * @see TelegramRequest#value
   */
  @AliasFor(value = "value", annotation = TelegramRequest.class)
  String[] value() default {};

  /**
   * @see TelegramRequest#pattern
   */
  @AliasFor(value = "pattern", annotation = TelegramRequest.class)
  String[] pattern() default {};

  /**
   * @see TelegramRequest#messageType
   */
  @AliasFor(value = "messageType", annotation = TelegramRequest.class)
  MessageType[] messageType() default {};

  /**
   * @see TelegramRequest#exclusiveMessageType
   */
  @AliasFor(value = "exclusiveMessageType", annotation = TelegramRequest.class)
  boolean exclusiveMessageType() default false;
}
