package io.github.drednote.telegram.handler.scenario.property;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.annotation.TelegramAdvice;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * The {@code TelegramScenario} annotation is annotation that marks a class as a handler for telegram scenario actions.
 *
 * @author Ivan Galushko
 * @see TelegramScenarioAction
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@BetaApi
public @interface TelegramScenario {

}
