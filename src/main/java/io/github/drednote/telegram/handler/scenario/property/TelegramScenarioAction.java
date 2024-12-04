package io.github.drednote.telegram.handler.scenario.property;

import io.github.drednote.telegram.core.annotation.BetaApi;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code TelegramScenarioAction} annotation is annotation that marks a method as a handler for telegram scenario
 * action.
 * <p>
 * This annotation worked in a pair with {@link TelegramScenario}. The class should be marked with
 * {@code TelegramScenario} and methods of this class, should be marked with {@code TelegramScenarioAction}
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * @TelegramScenario
 * public class TelegramSettingsFactory {
 *
 *     @TelegramScenarioAction
 *     public Object returnSettingsMenu(ActionContext<Object> context) throws Exception {
 *         // your code here
 *     }
 * }
 * }
 * </pre>
 * This method you can reference by {@code TelegramSettingsFactory#returnSettingsMenu}.
 *
 * @author Ivan Galushko
 * @see TelegramScenarioAction
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BetaApi
public @interface TelegramScenarioAction {

    /**
     * Name of reference. Default {@code Class#methodName} or {@code com.example.Class#methodName(String)} if parameter
     * {@link #fullName()} is true.
     *
     * @return name of reference
     */
    String value() default "";

    /**
     * Define when to use full name of method handler instead of simple name.
     * <p>
     * Full name - {@code com.example.Class#methodName(String)}
     * <p>
     * Simple - {@code Class#methodName}
     *
     * @return to use full class name or not
     */
    boolean fullName() default false;
}
