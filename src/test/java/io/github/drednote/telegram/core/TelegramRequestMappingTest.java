package io.github.drednote.telegram.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.core.request.RequestType;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class TelegramRequestMappingTest {

  @Test
  void shouldBe0WhenCallCompare() {
    TelegramRequestMapping telegramRequestMapping = new TelegramRequestMapping("Pattern",
        RequestType.MESSAGE, Collections.emptySet());
    assertEquals(0,
        telegramRequestMapping.compareTo(
            new TelegramRequestMapping("Pattern", RequestType.MESSAGE, Collections.emptySet())));
  }

  @Test
  void shouldBeLess0WhenCallCompare() {
    TelegramRequestMapping telegramRequestMapping = new TelegramRequestMapping("Pattern",
        RequestType.POLL, Collections.emptySet());
    assertEquals(1,
        telegramRequestMapping.compareTo(
            new TelegramRequestMapping("Pattern", RequestType.MESSAGE, Collections.emptySet())));
  }

  @Test
  void shouldBeMore0WhenCallCompare() {
    TelegramRequestMapping telegramRequestMapping = new TelegramRequestMapping("Pattern",
        RequestType.MESSAGE, Collections.emptySet());
    assertEquals(-1,
        telegramRequestMapping.compareTo(
            new TelegramRequestMapping("Pattern", RequestType.POLL, Collections.emptySet())));
  }
}

