package com.github.drednote.telegram.updatehandler.mvc.annotation;

import com.github.drednote.telegram.core.RequestType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BotRequest {

  @AliasFor("path")
  String[] value() default {};

  /**
   * applies only if update has text
   */
  @AliasFor("value")
  String[] path() default {};

  RequestType[] type() default {};

}
