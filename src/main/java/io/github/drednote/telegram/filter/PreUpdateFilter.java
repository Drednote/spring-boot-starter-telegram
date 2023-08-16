package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * Can be Bot Scope for keeping context during update handler
 */
public interface PreUpdateFilter extends UpdateFilterMatcher {

  void preFilter(@NonNull TelegramUpdateRequest request);

  default int getPreOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}