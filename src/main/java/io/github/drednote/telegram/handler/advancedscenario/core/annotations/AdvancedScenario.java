package io.github.drednote.telegram.handler.advancedscenario.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD}) // Можно применять к классам и методам
@Retention(RetentionPolicy.RUNTIME) // Доступна во время выполнения
public @interface AdvancedScenario {
    String name();
}
