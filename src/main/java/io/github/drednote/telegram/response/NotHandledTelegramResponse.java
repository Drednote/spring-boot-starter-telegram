package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;

/**
 * This class represents a {@code TelegramResponse} indicating that {@link UpdateRequest} was not handled. It sends a
 * message indicating that the command or text is unknown.
 *
 * @author Ivan Galushko
 * @see UpdateRequest
 */
public final class NotHandledTelegramResponse extends SimpleMessageTelegramResponse {

    /**
     * The singleton instance of NotHandledTelegramResponse
     *
     * @deprecated instead of singleton instance use constructor.
     */
    @Deprecated(since = "v0.6.0", forRemoval = true)
    public static final NotHandledTelegramResponse INSTANCE = new NotHandledTelegramResponse();

    public NotHandledTelegramResponse() {
        super("response.notHandled", "Unknown command or text, try something else");
    }
}
