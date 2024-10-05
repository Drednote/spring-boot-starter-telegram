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
 * The {@code TelegramMessage} annotation is a specialized form of the {@link TelegramRequest}
 * annotation that is used to define mappings for handling Telegram update requests related to
 * message. This annotation simplifies the process of creating message-related mappings by
 * automatically setting the messages like request type.
 * <p>
 * Methods annotated with {@code TelegramAnyMessage} will be invoked when incoming message updates
 * match the specified criteria, allowing the controller to process those updates accordingly.
 * <p>
 * This annotation is meant to be used at the method level and is particularly useful for methods
 * that handle messages within a {@link TelegramController}.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * @TelegramController
 * public class MyTelegramController {
 *
 *     @TelegramAnyMessage(pattern = "hello")
 *     public void handleHelloMessage(UpdateRequest update) {
 *         // Handle hello message logic
 *     }
 * }
 * }
 * </pre>
 * In the above example, the {@code handleHelloMessage} method will be invoked when an incoming
 * message update matches the pattern {@code "hello"}.
 *
 * @author Ivan Galushko
 * @see TelegramController
 * @see TelegramRequest
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TelegramRequest(exclusiveMessageType = false, requestType = {
    RequestType.MESSAGE, RequestType.EDITED_MESSAGE, RequestType.CHANEL_POST,
    RequestType.EDITED_CHANEL_POST, RequestType.BUSINESS_MESSAGE,
    RequestType.EDITED_BUSINESS_MESSAGE
})
public @interface TelegramAnyMessage {

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

    /**
     * @see TelegramRequest#messageType
     */
    @AliasFor(value = "messageType", annotation = TelegramRequest.class)
    MessageType[] messageType() default {};
}
