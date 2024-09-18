package io.github.drednote.telegram.core.request;

import java.util.Set;
import org.springframework.util.PathMatcher;

public interface UpdateRequestMappingAccessor {

    String getPattern();

    RequestType getRequestType();

    Set<MessageType> getMessageTypes();

    PathMatcher getPathMatcher();
}
