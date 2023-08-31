package io.github.drednote.telegram.core.comparator;

import io.github.drednote.telegram.utils.Assert;
import org.springframework.util.PathMatcher;

/**
 * A comparator that delegate comparing of two strings based to {@link PathMatcher}
 *
 * @author Ivan Galushko
 * @see PathMatcher
 */
public final class PatternComparator extends DefaultComparator<String> {

  private final PathMatcher delegate;

  public PatternComparator(PathMatcher delegate) {
    Assert.required(delegate, "PathMatcher");
    this.delegate = delegate;
  }

  /**
   * Compares two strings using the pattern comparator of the {@link PathMatcher}
   *
   * @param o1 the first string to compare, not null
   * @param o2 the second string to compare, not null
   * @return a negative integer, zero, or a positive integer
   * @see PathMatcher#getPatternComparator
   */
  @Override
  protected int doCompare(String o1, String o2) {
    return delegate.getPatternComparator(o1).compare(o1, o2);
  }
}
