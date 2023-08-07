package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * Can be Bot Scope for keeping context during update handler
 */
public interface UpdateFilter {

  default void preFilter(@NonNull ExtendedTelegramUpdateRequest request) {
    // do nothing
  }

  default void postFilter(@NonNull ExtendedTelegramUpdateRequest request) {
    // do nothing
  }

  default boolean matches(ExtendedTelegramUpdateRequest request) {
    return true;
  }

  default int getPreOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  default int getPostOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}