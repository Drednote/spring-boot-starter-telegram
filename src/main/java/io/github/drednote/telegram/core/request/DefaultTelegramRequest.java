package io.github.drednote.telegram.core.request;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DefaultTelegramRequest implements TelegramRequest {

    private Set<String> patterns;

    private Set<RequestType> requestTypes;

    private Set<MessageType> messageTypes = new HashSet<>();

    private boolean exclusiveMessageType = false;

    public DefaultTelegramRequest(Set<String> patterns, Set<RequestType> requestTypes) {
        this.patterns = patterns;
        this.requestTypes = requestTypes;
    }

    public DefaultTelegramRequest(Set<String> patterns, Set<RequestType> requestTypes, Set<MessageType> messageTypes) {
        this.patterns = patterns;
        this.requestTypes = requestTypes;
        this.messageTypes = messageTypes;
    }

    public DefaultTelegramRequest(
        Set<String> patterns, Set<RequestType> requestTypes, Set<MessageType> messageTypes, boolean exclusiveMessageType
    ) {
        this.patterns = patterns;
        this.requestTypes = requestTypes;
        this.messageTypes = messageTypes;
        this.exclusiveMessageType = exclusiveMessageType;
    }
}