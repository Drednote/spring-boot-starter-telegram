package io.github.drednote.telegram.core.request;

import java.util.Set;

/**
 * A condition that must be met for a given transition to be called. The matching is executed by
 * {@link UpdateRequestMapping}
 *
 * @author Ivan Galushko
 * @see io.github.drednote.telegram.core.annotation.TelegramRequest
 */
public interface TelegramRequest {

    /**
     * Retrieves the patterns associated with the {@link UpdateRequest}.
     *
     * @return a set of strings representing the patterns
     */
    Set<String> getPatterns();

    /**
     * The request types associated with the {@link UpdateRequest}.
     *
     * @return a set of request types
     * @see RequestType
     */
    Set<RequestType> getRequestTypes();

    /**
     * The message types associated with the {@link UpdateRequest}. These message types define the
     * types of messages that will trigger the annotated method.
     *
     * @return a set of message types
     * @see MessageType
     */
    Set<MessageType> getMessageTypes();

    /**
     * Determines how message types will be mapped. If set to {@code false}, the annotated method
     * will accept any message type specified in the {@link #getMessageTypes()} parameter. If set to
     * {@code true}, the method will only accept messages that have all the types listed in the
     * {@link #getMessageTypes()} parameter.
     * <p>
     * In simple words: {@code false} means any match, {@code true} means all matches.
     * <p>
     *
     * @return {@code true} to enforce exclusive message type mapping, {@code false} otherwise,
     * defaults to {@code false}
     */
    boolean isExclusiveMessageType();
}
