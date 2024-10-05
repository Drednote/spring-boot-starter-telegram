package io.github.drednote.telegram.filter.post;

import io.github.drednote.telegram.core.annotation.TelegramScope;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.UpdateFilterMatcher;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;


/**
 * Represents a post-update filter for Telegram update requests. <b>It executed before sending response to telegram</b>
 *
 * <p>This interface extends the {@link UpdateFilterMatcher} interface and is responsible for
 * performing post-filtering actions on an incoming Telegram update request after it has been processed by update
 * handlers and before sending response to telegram.
 *
 * <p>The {@link #getPostOrder()} method specifies the order in which this post-update filter
 * should be executed relative to other post-update filters. A lower value indicates a higher priority, and the default
 * order is set to {@link Ordered#LOWEST_PRECEDENCE}.
 *
 * @author Ivan Galushko
 * @apiNote <b>Can be Telegram Scope for keeping context during update handler</b>
 * @see TelegramScope
 */
public interface PostUpdateFilter extends UpdateFilterMatcher {

    /**
     * Post-filters the incoming Telegram update request after it has been processed by update handlers
     *
     * @param request The incoming Telegram update request to be post-filtered
     */
    void postFilter(@NonNull UpdateRequest request);

    /**
     * Gets the post-update filter's execution order
     *
     * @return The order in which this post-update filter should be executed
     */
    default int getPostOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    default boolean matches(UpdateRequest request) {
        return request.getResponse() == null || request.getResponse().isExecutePostFilters();
    }
}
