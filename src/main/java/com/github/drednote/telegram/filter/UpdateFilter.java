package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.request.ExtendedBotRequest;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * Can be Bot Scope for keeping context during update handler
 */
public interface UpdateFilter {

  default void preFilter(@NonNull ExtendedBotRequest request) {
    // do nothing
  }

  default void postFilter(@NonNull ExtendedBotRequest request) {
    // do nothing
  }

  default int getPreOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  default int getPostOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}