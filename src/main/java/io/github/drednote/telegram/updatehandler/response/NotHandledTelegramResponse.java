package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;

/**
 * This class represents a {@code TelegramResponse} indicating that {@link TelegramUpdateRequest}
 * was not handled. It sends a message indicating that the command or text is unknown.
 *
 * @author Ivan Galushko
 * @see TelegramUpdateRequest
 */
public final class NotHandledTelegramResponse extends SimpleMessageTelegramResponse {

  public static final NotHandledTelegramResponse INSTANCE = new NotHandledTelegramResponse();

  private NotHandledTelegramResponse() {
    super("response.notHandled", "Unknown command or text, try something else");
  }
}
