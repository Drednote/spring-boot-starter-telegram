package io.github.drednote.telegram.filter.post;

import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.response.SimpleMessageTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendResponseFilter implements ConclusivePostUpdateFilter {

    /**
     * The message source for retrieving localized messages
     */
    private final TelegramMessageSource messageSource;

    /**
     * @param messageSource the message source, not null
     */
    public SendResponseFilter(TelegramMessageSource messageSource) {
        Assert.required(messageSource, "TelegramMessageSource");

        this.messageSource = messageSource;
    }

    /**
     * Answers the update request by processing the response
     *
     * @param request the update request
     * @throws TelegramApiException if an error occurs during processing answer
     */
    @Override
    public void postFilter(UpdateRequest request) throws TelegramApiException {
        TelegramResponse response = request.getResponse();
        if (response != null) {
            if (response instanceof SimpleMessageTelegramResponse simpleMessageTelegramResponse) {
                simpleMessageTelegramResponse.setMessageSource(messageSource);
            }
            response.process(request);
        }
    }

    @Override
    public int getPostOrder() {
        return FilterOrder.CONCLUSIVE_POST_FILTERS.get(this.getClass());
    }
}
