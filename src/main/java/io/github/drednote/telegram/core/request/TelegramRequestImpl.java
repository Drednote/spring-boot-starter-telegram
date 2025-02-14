package io.github.drednote.telegram.core.request;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelegramRequestImpl implements TelegramRequest {

    private Set<String> patterns;

    private Set<RequestType> requestTypes;

    private Set<MessageType> messageTypes;

    private boolean exclusiveMessageType;

    @Override
    public boolean exclusiveMessageType() {
        return exclusiveMessageType;
    }
}
