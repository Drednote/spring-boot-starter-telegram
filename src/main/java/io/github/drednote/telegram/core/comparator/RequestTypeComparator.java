package io.github.drednote.telegram.core.comparator;

import io.github.drednote.telegram.core.request.RequestType;
import java.util.EnumMap;
import java.util.Map;

/**
 * This class is a comparator for {@link RequestType} objects. It compares two {@link RequestType}
 * objects based on their priority. Higher priority RequestType objects will be ranked higher in the
 * comparison
 *
 * <p>The priority of each {@link RequestType} is specified in the priority map
 *
 * @author Galushko Ivan
 */
public final class RequestTypeComparator extends DefaultComparator<RequestType> {

  private final Map<RequestType, Integer> priority = new EnumMap<>(RequestType.class);

  /**
   * Singleton instance of the RequestTypeComparator
   */
  public static final RequestTypeComparator INSTANCE = new RequestTypeComparator();

  /**
   * Initialize priority values for different RequestType constants
   *
   * @implNote maybe need to delete
   */
  private RequestTypeComparator() {
    priority.put(RequestType.MESSAGE, 1);
    priority.put(RequestType.INLINE_QUERY, 1);
    priority.put(RequestType.CHOSEN_INLINE_QUERY, 1);
    priority.put(RequestType.CALLBACK_QUERY, 1);
    priority.put(RequestType.SHIPPING_QUERY, 1);
    priority.put(RequestType.PRE_CHECKOUT_QUERY, 1);
    priority.put(RequestType.POLL, 1);
    priority.put(RequestType.POLL_ANSWER, 1);
    priority.put(RequestType.CHAT_MEMBER_UPDATED, 1);
    priority.put(RequestType.CHAT_JOIN_REQUEST, 1);
  }

  /**
   * Compares two RequestType objects based on their priority
   *
   * @param o1 The first RequestType to compare, not null
   * @param o2 The second RequestType to compare, not null
   * @return A negative integer, zero, or a positive integer as the priority of o2 is less than,
   * equal to, or greater than the priority of o1
   */
  @Override
  public int doCompare(RequestType o1, RequestType o2) {
    return priority.get(o2) - priority.get(o1);
  }
}
