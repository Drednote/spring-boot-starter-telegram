package com.github.drednote.telegram.core.matcher;

import com.github.drednote.telegram.core.request.TelegramUpdateRequest;
import com.github.drednote.telegram.core.request.TelegramRequestMapping;
import com.github.drednote.telegram.core.request.RequestType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;

@RequiredArgsConstructor
public class RequestTypeMatcher implements RequestMatcher {

  private final TelegramRequestMapping mapping;

  @Override
  public boolean matches(TelegramUpdateRequest request) {
    RequestType requestType = request.getRequestType();
    if (mapping.getRequestType() != null && requestType != mapping.getRequestType()) {
      return false;
    }
    return mapping.getMessageTypes().isEmpty()
        || request.getMessageTypes().containsAll(mapping.getMessageTypes());
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
