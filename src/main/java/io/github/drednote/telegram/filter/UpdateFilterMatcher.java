package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.post.ConclusivePostUpdateFilter;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import org.springframework.lang.NonNull;

/**
 * Represents an interface for matching Telegram update requests against certain criteria to be processed by
 * {@link PreUpdateFilter} or {@link ConclusivePostUpdateFilter}.
 *
 * <p>Designed specifically to work with {@link PreUpdateFilter}, {@link PostUpdateFilter} and
 * {@link ConclusivePostUpdateFilter}.
 *
 * <p>This interface provides a default implementation for the
 * {@link #matches(UpdateRequest)} method, which returns {@code true} by default. Classes implementing this interface
 * can override the default implementation to define custom logic for matching update requests.
 *
 * @author Ivan Galushko
 * @see PreUpdateFilter
 * @see ConclusivePostUpdateFilter
 */
public interface UpdateFilterMatcher {

    /**
     * Determines whether the given Telegram update request matches certain criteria to be processed by
     * {@link PreUpdateFilter}, {@link PostUpdateFilter} or {@link ConclusivePostUpdateFilter}.
     *
     * @param request The Telegram update request to be matched against criteria. Must not be null
     * @return {@code true} if the update request matches the criteria, {@code false} otherwise
     */
    default boolean matches(@NonNull UpdateRequest request) {
        return true;
    }
}
