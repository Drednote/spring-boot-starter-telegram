package com.github.drednote.telegram.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.drednote.telegram.core.request.RequestMappingInfo;
import com.github.drednote.telegram.core.request.RequestType;
import org.junit.jupiter.api.Test;

class RequestMappingInfoTest {

  @Test
  void shouldBe0WhenCallCompare() {
    RequestMappingInfo requestMappingInfo = new RequestMappingInfo("Pattern",
        RequestType.COMMAND);
    assertEquals(0,
        requestMappingInfo.compareTo(new RequestMappingInfo("Pattern", RequestType.COMMAND)));
  }

  @Test
  void shouldBeLess0WhenCallCompare() {
    RequestMappingInfo requestMappingInfo = new RequestMappingInfo("Pattern",
        RequestType.MESSAGE);
    assertEquals(1,
        requestMappingInfo.compareTo(new RequestMappingInfo("Pattern", RequestType.COMMAND)));
  }

  @Test
  void shouldBeMore0WhenCallCompare() {
    RequestMappingInfo requestMappingInfo = new RequestMappingInfo("Pattern",
        RequestType.COMMAND);
    assertEquals(-1,
        requestMappingInfo.compareTo(new RequestMappingInfo("Pattern", RequestType.MESSAGE)));
  }

  @Test
  void shouldThrowNullPointerIfPassNullToCompare() {
    assertThrows(NullPointerException.class,
        () -> new RequestMappingInfo("Pattern", RequestType.COMMAND).compareTo(null));
  }
}

