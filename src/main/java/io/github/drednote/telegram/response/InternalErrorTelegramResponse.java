package io.github.drednote.telegram.response;

/**
 * This class represents a {@code TelegramResponse} indicating an internal error occurred. It sends an internal error
 * message to the user.
 *
 * @author Ivan Galushko
 */
public final class InternalErrorTelegramResponse extends SimpleMessageTelegramResponse {

    /**
     * The singleton instance of InternalErrorTelegramResponse
     *
     * @deprecated instead of singleton instance use constructor.
     */
    @Deprecated(since = "v0.6.0", forRemoval = true)
    public static final InternalErrorTelegramResponse INSTANCE = new InternalErrorTelegramResponse();

    public InternalErrorTelegramResponse() {
        super("response.internalError", "Oops, something went wrong, please try again later.");
    }
}
