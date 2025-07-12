package io.github.drednote.telegram.response;

/**
 * This class represents a {@code TelegramResponse} indicating that there are too many requests. It sends a message
 * indicating that the user should try again later due to too many requests.
 *
 * @author Ivan Galushko
 */
public final class TooManyRequestsTelegramResponse extends SimpleMessageTelegramResponse {

    public TooManyRequestsTelegramResponse() {
        super("response.tooManyRequests", "Too many requests. Please try later");
    }

    @Override
    public boolean isExecutePostFilters() {
        return false;
    }
}
