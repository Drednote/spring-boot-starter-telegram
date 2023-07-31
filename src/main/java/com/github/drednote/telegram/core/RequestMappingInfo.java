package com.github.drednote.telegram.core;

import com.github.drednote.telegram.core.matcher.RequestMatcher;
import com.github.drednote.telegram.core.matcher.RequestMatcherFactory;
import com.github.drednote.telegram.utils.RequestTypeComparator;
import java.util.Objects;
import lombok.Getter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class RequestMappingInfo implements Comparable<RequestMappingInfo>, RequestMatcher {

  @Getter
  private final String pattern;
  @Getter
  private final RequestType type;

  @Getter
  private final PathMatcher pathMatcher = new AntPathMatcher();
  private final RequestMatcher requestMatcher;
  private final RequestTypeComparator requestTypeComparator = new RequestTypeComparator();

  public RequestMappingInfo(String pattern, RequestType type) {
    this.pattern = pattern;
    this.type = type;
    this.requestMatcher = RequestMatcherFactory.create(this);
  }

  public RequestMappingInfo getMatchingCondition(String requestText) {
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
  public int compareTo(RequestMappingInfo o) {
    int compare = requestTypeComparator.compare(this.type, o.type);
    if (compare == 0) {
      compare = pathMatcher.getPatternComparator(pattern)
          .compare(this.pattern, o.pattern);
    }
    return compare;
  }

  @Override
  public boolean matches(BotRequest request) {
    return requestMatcher.matches(request);
  }

  @Override
  public String toString() {
    return "{%s %s}".formatted(type != null ? type : "ALL", pattern);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestMappingInfo that = (RequestMappingInfo) o;
    return Objects.equals(pattern, that.pattern) && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pattern, type);
  }
}
