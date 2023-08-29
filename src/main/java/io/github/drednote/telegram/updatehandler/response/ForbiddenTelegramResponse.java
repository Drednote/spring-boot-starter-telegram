package io.github.drednote.telegram.updatehandler.response;

/**
 * This class represents a {@code TelegramResponse} indicating that the user does not have access to
 * the bot. It sends a forbidden message to the user.
 *
 * @author Ivan Galushko
 */
public final class ForbiddenTelegramResponse extends SimpleMessageTelegramResponse {

  /**
   * The singleton instance of ForbiddenTelegramResponse
   */
  public static final ForbiddenTelegramResponse INSTANCE = new ForbiddenTelegramResponse();

  private ForbiddenTelegramResponse() {
    super("response.forbidden", "You do not have access to this bot!");
  }
}
