package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;

/**
 * This class represents an empty {@code TelegramResponse} that performs no action when executed. It
 * is often used as a placeholder or default response when no specific action is needed.
 *
 * @author Ivan Galushko
 */
public final class EmptyTelegramResponse implements TelegramResponse {

  /**
   * The singleton instance of EmptyTelegramResponse
   */
  public static final EmptyTelegramResponse INSTANCE = new EmptyTelegramResponse();

  private EmptyTelegramResponse() {
    // Private constructor to enforce the singleton pattern.
  }

  /**
   * Does nothing when executed.
   *
   * @param updateRequest The TelegramUpdateRequest containing the update information.
   */
  @Override
  public void process(TelegramUpdateRequest updateRequest) {
    // do nothing
  }
}
