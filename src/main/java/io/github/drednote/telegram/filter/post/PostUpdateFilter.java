package io.github.drednote.telegram.filter.post;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.filter.UpdateFilterMatcher;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * Can be Bot Scope for keeping context during update handler
 */
public interface PostUpdateFilter extends UpdateFilterMatcher {

  void postFilter(@NonNull TelegramUpdateRequest request);

  default int getPostOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}