package com.github.drednote.telegram.core.request;

import com.github.drednote.telegram.core.comparator.RequestMappingInfoComparator;
import com.github.drednote.telegram.core.matcher.RequestMatcher;
import com.github.drednote.telegram.core.matcher.RequestMatcherFactory;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@RequiredArgsConstructor
public class TelegramRequestMapping implements Comparable<TelegramRequestMapping>, RequestMatcher {

  @Getter
  private final String pattern;
  @Getter
  @Nullable
  private final RequestType requestType;
  @Getter
  @NonNull
  private final Set<MessageType> messageTypes;

  @Getter
  private final PathMatcher pathMatcher = new AntPathMatcher();
  private final RequestMatcher requestMatcher = RequestMatcherFactory.create(this);
  private final RequestMappingInfoComparator comparator = new RequestMappingInfoComparator(
      pathMatcher);

  public TelegramRequestMapping getMatchingCondition(String requestText) {
    if (requestText == null) {
      requestText = "";
    }
    String matches = getMatchingPattern(pattern, requestText);

    return matches == null ? null : this;
  }

  private String getMatchingPattern(String pattern, String lookupPath) {
    if (pattern.equals(lookupPath)) {
      return pattern;
    }
    if (this.pathMatcher.match(pattern, lookupPath)) {
      return pattern;
    }
    return null;
  }

  @Override
  public int compareTo(@NonNull TelegramRequestMapping o) {
    return comparator.compare(this, o);
  }

  @Override
  public boolean matches(TelegramUpdateRequest request) {
    return requestMatcher.matches(request);
  }

  @Override
  public String toString() {
    return "{%s %s %s}".formatted(
        requestType != null ? requestType : "ALL",
        !messageTypes.isEmpty() ? messageTypes : "ALL",
        pattern
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TelegramRequestMapping that = (TelegramRequestMapping) o;
    return Objects.equals(pattern, that.pattern) && requestType == that.requestType
        && messageTypes.equals(that.messageTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pattern, requestType, messageTypes);
  }
}
