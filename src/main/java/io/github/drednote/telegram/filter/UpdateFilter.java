package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * Can be Bot Scope for keeping context during update handler
 */
public interface UpdateFilter {

  default void preFilter(@NonNull TelegramUpdateRequest request) {
    // do nothing
  }

  default void postFilter(@NonNull TelegramUpdateRequest request) {
    // do nothing
  }

  default boolean matches(TelegramUpdateRequest request) {
    return true;
  }

  default int getPreOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  default int getPostOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}