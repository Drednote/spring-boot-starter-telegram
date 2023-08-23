package io.github.drednote.telegram.core.matcher;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * The {@code RequestMatcher} interface represents a matcher for Telegram update requests. It
 * provides a method to check whether a given request matches the implementation's criteria. It also
 * extends the Ordered interface to specify the order of matchers
 */
@FunctionalInterface
public interface RequestMatcher extends Ordered {

  /**
   * Checks whether the given update request matches the criteria of the implementation
   *
   * @param request the update request to match, not null
   * @return true if the request matches, false otherwise
   */
  boolean matches(@NonNull TelegramUpdateRequest request);

  /**
   * Returns a composite matcher that combines this matcher with another matcher
   *
   * @param other the other matcher to combine with, not null
   * @return a composite matcher
   */
  default RequestMatcher thenMatching(@NonNull RequestMatcher other) {
    Assert.notNull(other, "Another RequestMatcher");
    return request -> matches(request) && other.matches(request);
  }

  /**
   * Returns the order of the matcher
   *
   * @return the order of the matcher
   */
  @Override
  default int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
