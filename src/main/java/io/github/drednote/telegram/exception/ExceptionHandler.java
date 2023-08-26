package io.github.drednote.telegram.exception;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;

/**
 * The {@code ExceptionHandler} interface describes a contract for classes that handle exceptions
 * that may occur during the processing of a TelegramUpdateRequest
 *
 * @author Ivan Galushko
 */
public interface ExceptionHandler {

  /**
   * Handles exceptions that occur during the processing of a {@code TelegramUpdateRequest}
   *
   * @param request the {@code TelegramUpdateRequest} object representing the request to be
   *                processed
   */
  void handle(TelegramUpdateRequest request);
}
