package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;

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
   * @param updateRequest The UpdateRequest containing the update information.
   */
  @Override
  public void process(UpdateRequest updateRequest) {
    // do nothing
  }
}
