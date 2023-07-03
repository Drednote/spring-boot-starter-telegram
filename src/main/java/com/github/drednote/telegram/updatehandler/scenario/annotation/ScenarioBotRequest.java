package com.github.drednote.telegram.updatehandler.scenario.annotation;

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
public @interface ScenarioBotRequest {

  @AliasFor("position")
  int value() default Integer.MIN_VALUE;

  @AliasFor("value")
  int position() default Integer.MIN_VALUE;

  RequestType[] type() default {};
}
