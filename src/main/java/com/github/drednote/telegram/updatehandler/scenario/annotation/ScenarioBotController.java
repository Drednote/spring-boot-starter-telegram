package com.github.drednote.telegram.updatehandler.scenario.annotation;

import com.github.drednote.telegram.core.RequestType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ScenarioBotController {

  String[] value() default {};

  RequestType[] type() default {};
}
