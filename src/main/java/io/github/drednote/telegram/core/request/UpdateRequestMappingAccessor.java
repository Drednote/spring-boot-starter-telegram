package io.github.drednote.telegram.core.request;

import java.util.Set;

public interface UpdateRequestMappingAccessor {

    String getPattern();

    RequestType getRequestType();

    Set<MessageType> getMessageTypes();
}
