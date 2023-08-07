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
@TelegramRequest(requestType = RequestType.MESSAGE, messageType = MessageType.COMMAND)
public @interface TelegramCommand {

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
}
