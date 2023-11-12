package io.github.drednote.telegram.handler.controller.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasRole {

  String[] value();

  StrategyMatching strategyMatching() default StrategyMatching.INTERSECTION;

  enum StrategyMatching {
    INTERSECTION, COMPLETE_MATCH
  }
}
