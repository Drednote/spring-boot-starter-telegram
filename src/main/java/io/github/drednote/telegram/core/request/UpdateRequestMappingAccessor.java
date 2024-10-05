package io.github.drednote.telegram.core.request;

import java.util.Set;
import org.springframework.util.PathMatcher;

/**
 * The {@code UpdateRequestMappingAccessor} class used for getting information about
 * {@link UpdateRequest} mapping.
 *
 * @author Ivan Galushko
 * @see UpdateRequestMapping
 */
public interface UpdateRequestMappingAccessor {

    /**
     * The pattern associated with the mapping
     */
    String getPattern();

    /**
     * The type of the request associated with the mapping
     */
    RequestType getRequestType();

    /**
     * The types of messages associated with the mapping
     */
    Set<MessageType> getMessageTypes();

    /**
     * The path matcher used for matching patterns
     */
    PathMatcher getPathMatcher();
}
