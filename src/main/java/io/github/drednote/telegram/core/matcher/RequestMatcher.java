package io.github.drednote.telegram.core.matcher;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import java.util.Objects;
import org.springframework.core.Ordered;

public interface RequestMatcher extends Ordered {

  boolean matches(TelegramUpdateRequest request);

  default RequestMatcher thenMatching(RequestMatcher other) {
    Objects.requireNonNull(other);
    return request -> matches(request) && other.matches(request);
  }

  @Override
  default int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
