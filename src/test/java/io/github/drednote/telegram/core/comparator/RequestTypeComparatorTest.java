package io.github.drednote.telegram.core.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.drednote.telegram.core.request.RequestType;
import org.junit.jupiter.api.Test;

class RequestTypeComparatorTest {

  /**
   * Method under test: {@link RequestTypeComparator#compare(RequestType, RequestType)}
   */
  @Test
  void shouldCorrectCompareRequestTypes() {
    RequestTypeComparator comparator = RequestTypeComparator.INSTANCE;

    assertEquals(1, comparator.compare(null, RequestType.MESSAGE));
    assertEquals(-1, comparator.compare(RequestType.MESSAGE, null));
    assertEquals(0, comparator.compare(RequestType.MESSAGE, RequestType.MESSAGE));

    assertEquals(1,
        comparator.compare(RequestType.INLINE_QUERY, RequestType.MESSAGE));
  }
}

