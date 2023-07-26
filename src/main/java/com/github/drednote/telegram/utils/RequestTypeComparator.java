package com.github.drednote.telegram.utils;

import com.github.drednote.telegram.core.RequestType;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public class RequestTypeComparator implements Comparator<RequestType> {

  private final Map<RequestType, Integer> priority = new EnumMap<>(RequestType.class);

  public RequestTypeComparator() {
    priority.put(RequestType.COMMAND, 11);
    priority.put(RequestType.MESSAGE, 10);
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
  public int compare(RequestType o1, RequestType o2) {
    if (o1 == null) {
      return 1;
    }
    if (o2 == null) {
      return -1;
    }
    return priority.get(o2) - priority.get(o1);
  }
}
