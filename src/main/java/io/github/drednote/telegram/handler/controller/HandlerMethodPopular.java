package io.github.drednote.telegram.handler.controller;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.springframework.lang.NonNull;

/**
 * The {@code HandlerMethodPopular} functional interface represents a mechanism for populating a
 * {@link TelegramUpdateRequest} with appropriate handler method to invoke.
 *
 * @author Ivan Galushko
 * @see ControllerUpdateHandler
 */
@FunctionalInterface
public interface HandlerMethodPopular {

  /**
   * populate a {@code TelegramUpdateRequest} with appropriate handler method to invoke.
   *
   * @param descriptor Telegram update request, not null
   */
  void populate(@NonNull TelegramUpdateRequest descriptor);
}
