package io.github.drednote.telegram.handler.advancedscenario.core.annotations;

import io.github.drednote.telegram.core.annotation.TelegramScope;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TelegramScope
@Component
@Conditional(AdvancedScenarioCondition.class)
public @interface AdvancedScenarioController {
    String name();
}
