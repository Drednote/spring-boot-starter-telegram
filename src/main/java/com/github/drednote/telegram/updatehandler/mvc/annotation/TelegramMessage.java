package com.github.drednote.telegram.updatehandler.mvc.annotation;

import com.github.drednote.telegram.core.request.MessageType;
import com.github.drednote.telegram.core.request.RequestType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

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
   * @see TelegramRequest#path
   */
  @AliasFor(value = "path", annotation = TelegramRequest.class)
  String[] path() default {};

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
