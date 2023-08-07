package io.github.drednote.telegram.core.comparator;

import lombok.RequiredArgsConstructor;
import org.springframework.util.PathMatcher;

@RequiredArgsConstructor
public final class PatternComparator extends DefaultComparator<String> {

  private final PathMatcher pathMatcher;

  @Override
  protected int doCompare(String o1, String o2) {
    return pathMatcher.getPatternComparator(o1).compare(o1, o2);
  }
}
