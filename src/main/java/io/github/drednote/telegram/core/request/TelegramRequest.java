package io.github.drednote.telegram.core.request;

import java.util.Set;

public interface TelegramRequest {

    Set<String> getPatterns();

    Set<RequestType> getRequestTypes();

    Set<MessageType> getMessageTypes();

    boolean exclusiveMessageType();
}
