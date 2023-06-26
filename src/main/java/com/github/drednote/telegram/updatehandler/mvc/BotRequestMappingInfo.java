package com.github.drednote.telegram.updatehandler.mvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@ToString
@EqualsAndHashCode
@Getter
public class BotRequestMappingInfo {

  private final Set<String> patterns;
  private final PathMatcher pathMatcher;

  private BotRequestMappingInfo(Collection<String> patterns) {
    this.patterns = new HashSet<>(patterns);
    this.pathMatcher = new AntPathMatcher();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String[] path;

    private Builder() {
    }

    public Builder path(String... val) {
      path = val;
      return this;
    }

    public BotRequestMappingInfo build() {
      return new BotRequestMappingInfo(asList(path));
    }

    @SafeVarargs
    private <T> List<T> asList(T... patterns) {
      return (patterns != null ? Arrays.asList(patterns) : Collections.emptyList());
    }
  }

  public BotRequestMappingInfo getMatchingCondition(String requestText) {
    if (this.patterns.isEmpty()) {
      return this;
    }
    if (requestText == null) {
      requestText = "";
    }
    List<String> matches = getMatchingPatterns(requestText);

    return matches.isEmpty() ? null : this;
  }

  public List<String> getMatchingPatterns(String lookupPath) {
    List<String> matches = new ArrayList<>();
    for (String pattern : this.patterns) {
      String match = getMatchingPattern(pattern, lookupPath);
      if (match != null) {
        matches.add(match);
      }
    }
    matches.sort(this.pathMatcher.getPatternComparator(lookupPath));
    return matches;
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
}
