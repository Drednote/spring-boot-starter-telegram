package io.github.drednote.telegram.response;

/**
 * This class represents a {@code TelegramResponse} indicating an internal error occurred. It sends
 * an internal error message to the user.
 *
 * @author Ivan Galushko
 */
public final class InternalErrorTelegramResponse extends SimpleMessageTelegramResponse {

  /**
   * The singleton instance of InternalErrorTelegramResponse
   */
  public static final InternalErrorTelegramResponse INSTANCE = new InternalErrorTelegramResponse();

  private InternalErrorTelegramResponse() {
    super("response.internalError", "Oops, something went wrong, please try again later.");
  }
}
