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
 * The {@code TelegramRequest} annotation is used to define mappings for handling specific types of
 * Telegram update requests. This annotation provides a way to associate methods in a
 * {@link TelegramController} class with incoming Telegram updates based on their patterns, request
 * types, and message types.
 * <p>
 * Methods annotated with {@code TelegramRequest} will be invoked when incoming updates match the
 * specified criteria, allowing the controller to process those updates accordingly.
 * <p>
 * This annotation is meant to be used at the method level.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * @TelegramController
 * public class MyTelegramController {
 *
 *     @TelegramRequest(pattern = "/start")
 *     public void handleStartCommand(TelegramUpdateRequest update) {
 *         // Handle start command logic
 *     }
 * }
 * }
 * </pre>
 * In the above example, the {@code handleStartCommand} method will be invoked when an incoming
 * update matches the pattern {@code "/start"}.
 *
 * @author Ivan Galushko
 * @see TelegramController
 * @see TelegramMessage
 * @see TelegramCommand
 * @see TelegramPatternVariable
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TelegramRequest {

  /**
   * The patterns associated with the mapping. These patterns define the specific updates that will
   * trigger the annotated method. Multiple patterns can be provided.
   *
   * @return an array of patterns to match, defaults to an empty array
   */
  @AliasFor("pattern")
  String[] value() default {};

  /**
   * Alias for {@link #value()}. Defines the patterns associated with the mapping.
   *
   * @return an array of patterns to match, defaults to an empty array
   */
  @AliasFor("value")
  String[] pattern() default {};

  /**
   * The request types associated with the mapping. These request types specify the types of updates
   * that will trigger the annotated method.
   *
   * @return an array of request types, defaults to an empty array
   * @see RequestType
   */
  RequestType[] requestType() default {};

  /**
   * The message types associated with the mapping. These message types define the types of messages
   * that will trigger the annotated method. This parameter is only applied if the
   * {@link #requestType()} is set to {@link RequestType#MESSAGE} or is not specified.
   *
   * @return an array of message types, defaults to an empty array
   * @see MessageType
   */
  MessageType[] messageType() default {};

  /**
   * Determines how message types will be mapped. If set to {@code false}, the annotated method will
   * accept any message type specified in the {@link #messageType()} parameter. If set to
   * {@code true}, the method will only accept messages that have all the types listed in the
   * {@link #messageType()} parameter.
   * <p>
   * In simple words: {@code false} means any match, {@code true} means all match.
   * <p>
   * This parameter is not used if {@link #messageType()} is empty.
   *
   * @return {@code true} to enforce exclusive message type mapping, {@code false} otherwise,
   * defaults to {@code false}
   */
  boolean exclusiveMessageType() default false;
}
