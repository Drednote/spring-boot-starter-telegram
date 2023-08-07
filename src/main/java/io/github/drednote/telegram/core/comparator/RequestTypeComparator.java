package io.github.drednote.telegram.core.comparator;

import io.github.drednote.telegram.core.request.RequestType;
import java.util.EnumMap;
import java.util.Map;

public final class RequestTypeComparator extends DefaultComparator<RequestType> {

  private final Map<RequestType, Integer> priority = new EnumMap<>(RequestType.class);

  public static final RequestTypeComparator INSTANCE = new RequestTypeComparator();

  private RequestTypeComparator() {
    // maybe need to delete
    priority.put(RequestType.MESSAGE, 2);
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

  @Override
  public int doCompare(RequestType o1, RequestType o2) {
    return priority.get(o2) - priority.get(o1);
  }
}
