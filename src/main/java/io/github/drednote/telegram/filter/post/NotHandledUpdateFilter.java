package io.github.drednote.telegram.filter.post;

import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.response.NotHandledTelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import org.springframework.lang.NonNull;

/**
 * Implementation of a post-update filter for handling not handled Telegram update requests.
 *
 * <p>This class implements the {@link ConclusivePostUpdateFilter} interface and is responsible for checking
 * if an incoming Telegram update request has not been handled. If the update request does not have a response and the
 * default answer is enabled in the properties, this filter sets the {@link NotHandledTelegramResponse} as the response
 * for the update request.
 *
 * @author Ivan Galushko
 * @see NotHandledTelegramResponse
 */
public class NotHandledUpdateFilter implements PostUpdateFilter {

    private final TelegramProperties telegramProperties;

    public NotHandledUpdateFilter(TelegramProperties telegramProperties) {
        Assert.required(telegramProperties, "TelegramProperties");
        this.telegramProperties = telegramProperties;
    }

    /**
     * Post-filters the incoming Telegram update request to handle not handled cases.
     *
     * <p>If the update request does not have a response and the default answer is enabled in the
     * properties, this method sets the {@link NotHandledTelegramResponse} as the response for the update request.
     *
     * @param request The incoming Telegram update request to be post-filtered, not null
     */
    @Override
    public void postFilter(@NonNull UpdateRequest request) {
        Assert.notNull(request, "UpdateRequest");
        if (telegramProperties.getFilters().isSetDefaultAnswer()) {
            request.getAccessor().setResponse(new NotHandledTelegramResponse());
        }
    }

    @Override
    public boolean matches(UpdateRequest request) {
        return request.getResponse() == null;
    }

    @Override
    public int getPostOrder() {
        return FilterOrder.NOT_HANDLED_FILTER_PRECEDENCE;
    }
}
