package io.github.drednote.telegram.response;

/**
 * This class represents a {@code TelegramResponse} indicating that the user does not have access to the bot. It sends a
 * forbidden message to the user.
 *
 * @author Ivan Galushko
 */
public final class ForbiddenTelegramResponse extends SimpleMessageTelegramResponse {

    /**
     * The singleton instance of ForbiddenTelegramResponse
     *
     * @deprecated instead of singleton instance use constructor.
     */
    @Deprecated(since = "v0.6.0", forRemoval = true)
    public static final ForbiddenTelegramResponse INSTANCE = new ForbiddenTelegramResponse();

    public ForbiddenTelegramResponse() {
        super("response.forbidden", "You do not have access to this bot!");
    }

    @Override
    public boolean isExecutePostFilters() {
        return false;
    }
}
