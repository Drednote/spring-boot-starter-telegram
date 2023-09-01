package io.github.drednote.telegram.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * The {@code TelegramController} annotation is used to indicate that a class is a Telegram
 * controller. This annotation serves as a specialization of the {@link Component} annotation,
 * allowing the class to be automatically detected and registered as a Spring bean during component
 * scanning. Telegram controllers handle incoming Telegram update requests and define methods to
 * process them.
 * <p>
 * Classes annotated with {@code TelegramController} will be scanned and instantiated as beans by
 * the Spring application context. These beans can then be used to process various types of Telegram
 * updates.
 * <p>
 * This annotation is meant to be used at the class level.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * @TelegramController
 * public class MyTelegramController {
 *     // Controller methods and logic
 * }
 * }
 * </pre>
 * The above example demonstrates how to define a Telegram controller using the
 * {@code TelegramController} annotation.
 * <p>
 * Please note that the presence of this annotation alone does not define the behavior of the
 * controller methods. Controller methods should be further annotated with specific request mapping
 * annotations, such as {@link TelegramRequest}, to indicate how they handle specific types of
 * incoming Telegram updates.
 * <p>
 * The {@code TelegramController} annotation is retained at runtime and can be accessed through
 * reflection.
 *
 * @author Ivan Galushko
 * @see TelegramRequest
 * @see TelegramPatternVariable
 * @see Component
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface TelegramController {

}
