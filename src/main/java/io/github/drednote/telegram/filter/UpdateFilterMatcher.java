package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;

public interface UpdateFilterMatcher {

  default boolean matches(TelegramUpdateRequest request) {
    return true;
  }
}
