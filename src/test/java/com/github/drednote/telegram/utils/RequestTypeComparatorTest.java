package com.github.drednote.telegram.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.drednote.telegram.core.RequestType;
import org.junit.jupiter.api.Test;

class RequestTypeComparatorTest {

  /**
   * Method under test: {@link RequestTypeComparator#compare(RequestType, RequestType)}
   */
  @Test
  void shouldCorrectCompareRequestTypes() {
    RequestTypeComparator comparator = new RequestTypeComparator();

    assertEquals(0,
        comparator.compare(RequestType.COMMAND, RequestType.COMMAND));
    assertEquals(1, comparator.compare(null, RequestType.COMMAND));
    assertEquals(-1, comparator.compare(RequestType.COMMAND, null));
    assertEquals(1,
        comparator.compare(RequestType.MESSAGE, RequestType.COMMAND));

    assertEquals(10,
        comparator.compare(RequestType.INLINE_QUERY, RequestType.COMMAND));
    assertEquals(10, comparator.compare(RequestType.CHOSEN_INLINE_QUERY,
        RequestType.COMMAND));
    assertEquals(10,
        comparator.compare(RequestType.CALLBACK_QUERY, RequestType.COMMAND));
    assertEquals(10,
        comparator.compare(RequestType.SHIPPING_QUERY, RequestType.COMMAND));
    assertEquals(10,
        comparator.compare(RequestType.PRE_CHECKOUT_QUERY, RequestType.COMMAND));
    assertEquals(10, comparator.compare(RequestType.POLL, RequestType.COMMAND));
    assertEquals(10,
        comparator.compare(RequestType.POLL_ANSWER, RequestType.COMMAND));
    assertEquals(10, comparator.compare(RequestType.CHAT_MEMBER_UPDATED,
        RequestType.COMMAND));
    assertEquals(10,
        comparator.compare(RequestType.CHAT_JOIN_REQUEST, RequestType.COMMAND));
  }
}

