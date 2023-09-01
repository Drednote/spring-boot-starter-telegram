package io.github.drednote.telegram.core.annotation;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * The {@code TelegramCommand} annotation is a specialized form of the {@link TelegramRequest}
 * annotation that is used to define mappings for handling Telegram update requests related to
 * command messages. This annotation simplifies the process of creating command-related mappings by
 * automatically setting the {@link RequestType#MESSAGE} request type and specifying the
 * {@link MessageType#COMMAND} message type.
 * <p>
 * Methods annotated with {@code TelegramCommand} will be invoked when incoming command message
 * updates match the specified criteria, allowing the controller to process those updates
 * accordingly.
 * <p>
 * This annotation is meant to be used at the method level and is particularly useful for methods
 * that handle command messages within a {@link TelegramController}.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * @TelegramController
 * public class MyTelegramController {
 *
 *     @TelegramCommand("/start")
 *     public void handleStartCommand(TelegramUpdateRequest update) {
 *         // Handle start command logic
 *     }
 * }
 * }
 * </pre>
 * In the above example, the {@code handleStartCommand} method will be invoked when an incoming
 * command message update matches the pattern {@code "/start"}.
 *
 * @author Ivan Galushko
 * @see TelegramController
 * @see TelegramRequest
 */
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
   * @see TelegramRequest#pattern
   */
  @AliasFor(value = "pattern", annotation = TelegramRequest.class)
  String[] pattern() default {};
}
