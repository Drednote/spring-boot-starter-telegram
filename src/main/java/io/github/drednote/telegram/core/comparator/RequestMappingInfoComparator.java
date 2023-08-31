package io.github.drednote.telegram.core.comparator;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.utils.Assert;
import java.util.Comparator;
import java.util.Set;
import org.springframework.util.PathMatcher;

/**
 * A comparator for sorting {@link TelegramRequestMapping} objects based on delegators
 * <p>
 * Delegators - other comparators, that compare specific fields in {@link TelegramRequestMapping}
 *
 * @author Ivan Galushko
 * @see RequestTypeComparator
 * @see MessageTypeComparator
 * @see PatternComparator
 */
public class RequestMappingInfoComparator extends DefaultComparator<TelegramRequestMapping> {

  private final Comparator<String> patternComparator;
  private static final Comparator<RequestType> requestTypeComparator = RequestTypeComparator.INSTANCE;
  private static final Comparator<Set<MessageType>> messageTypeComparator = MessageTypeComparator.INSTANCE;

  /**
   * Creates a new RequestMappingInfoComparator with the given PathMatcher
   *
   * @param pathMatcher The PathMatcher used to compare pattern strings, not null
   */
  public RequestMappingInfoComparator(PathMatcher pathMatcher) {
    Assert.required(pathMatcher, "PathMatcher");
    this.patternComparator = new PatternComparator(pathMatcher);
  }

  /**
   * Compares two TelegramRequestMapping objects based on their priority and other criteria.
   *
   * @param first  The first TelegramRequestMapping to compare, not null
   * @param second The second TelegramRequestMapping to compare, not null
   * @return A negative integer, zero, or a positive integer as the first object is less than, equal
   * to, or greater than the second object
   * @throws NullPointerException if either of the parameters is null
   */
  @Override
  public int doCompare(TelegramRequestMapping first, TelegramRequestMapping second) {
    int compare = requestTypeComparator.compare(first.getRequestType(), second.getRequestType());
    if (compare == 0) {
      compare = messageTypeComparator.compare(first.getMessageTypes(), second.getMessageTypes());
    }
    if (compare == 0) {
      compare = patternComparator.compare(first.getPattern(), second.getPattern());
    }
    return compare;
  }
}
