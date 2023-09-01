package io.github.drednote.telegram.handler.controller;

import io.github.drednote.telegram.core.request.UpdateRequest;
import org.springframework.lang.NonNull;

/**
 * The {@code HandlerMethodPopular} functional interface represents a mechanism for populating a
 * {@link UpdateRequest} with appropriate handler method to invoke.
 *
 * @author Ivan Galushko
 * @see ControllerUpdateHandler
 */
@FunctionalInterface
public interface HandlerMethodPopular {

  /**
   * populate a {@code UpdateRequest} with appropriate handler method to invoke.
   *
   * @param descriptor Telegram update request, not null
   */
  void populate(@NonNull UpdateRequest descriptor);
}
