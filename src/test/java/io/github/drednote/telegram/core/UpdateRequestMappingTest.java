package io.github.drednote.telegram.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.RequestType;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class UpdateRequestMappingTest {

  @Test
  void shouldBe0WhenCallCompare() {
    UpdateRequestMapping updateRequestMapping = new UpdateRequestMapping("Pattern",
        RequestType.MESSAGE, Collections.emptySet());
    assertEquals(0,
        updateRequestMapping.compareTo(
            new UpdateRequestMapping("Pattern", RequestType.MESSAGE, Collections.emptySet())));
  }

  @Test
  void shouldBeLess0WhenCallCompare() {
    UpdateRequestMapping updateRequestMapping = new UpdateRequestMapping("Pattern",
        RequestType.POLL, Collections.emptySet());
    assertEquals(0,
        updateRequestMapping.compareTo(
            new UpdateRequestMapping("Pattern", RequestType.MESSAGE, Collections.emptySet())));
  }

  @Test
  void shouldBeMore0WhenCallCompare() {
    UpdateRequestMapping updateRequestMapping = new UpdateRequestMapping("Pattern",
        RequestType.MESSAGE, Collections.emptySet());
    assertEquals(0,
        updateRequestMapping.compareTo(
            new UpdateRequestMapping("Pattern", RequestType.POLL, Collections.emptySet())));
  }
}

