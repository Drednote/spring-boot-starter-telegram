package com.github.drednote.telegram.core.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.github.drednote.telegram.core.request.TelegramUpdateRequest;
import com.github.drednote.telegram.core.request.MessageType;
import com.github.drednote.telegram.core.request.TelegramRequestMapping;
import com.github.drednote.telegram.core.request.RequestType;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RequestTypeMatcherTest {

  @Test
  void shouldMatchRequestType() {
    RequestTypeMatcher matcher = new RequestTypeMatcher(
        new TelegramRequestMapping(null, RequestType.POLL, Collections.emptySet()));
    // mock
    TelegramUpdateRequest request = Mockito.mock(TelegramUpdateRequest.class);
    when(request.getMessageTypes()).thenReturn(Collections.emptySet());

    when(request.getRequestType()).thenReturn(RequestType.POLL);
    assertThat(matcher.matches(request)).isTrue();

    when(request.getRequestType()).thenReturn(RequestType.MESSAGE);
    assertThat(matcher.matches(request)).isFalse();

    matcher = new RequestTypeMatcher(
        new TelegramRequestMapping(null, null, Collections.emptySet()));
    assertThat(matcher.matches(request)).isTrue();
  }

  @Test
  void shouldMatchMessageType() {
    RequestTypeMatcher matcher = new RequestTypeMatcher(
        new TelegramRequestMapping(null, RequestType.MESSAGE, Set.of(MessageType.COMMAND)));
    // mock
    TelegramUpdateRequest request = Mockito.mock(TelegramUpdateRequest.class);
    when(request.getRequestType()).thenReturn(RequestType.MESSAGE);

    when(request.getMessageTypes()).thenReturn(Collections.emptySet());
    assertThat(matcher.matches(request)).isFalse();

    when(request.getMessageTypes()).thenReturn(Set.of(MessageType.COMMAND));
    assertThat(matcher.matches(request)).isTrue();

    matcher = new RequestTypeMatcher(
        new TelegramRequestMapping(null, RequestType.MESSAGE,
            Set.of(MessageType.COMMAND, MessageType.PHOTO)));
    when(request.getMessageTypes()).thenReturn(Set.of(MessageType.COMMAND));
    assertThat(matcher.matches(request)).isFalse();

    when(request.getMessageTypes()).thenReturn(Set.of(MessageType.COMMAND, MessageType.PHOTO));
    assertThat(matcher.matches(request)).isTrue();

    when(request.getMessageTypes()).thenReturn(
        Set.of(MessageType.COMMAND, MessageType.PHOTO, MessageType.SERVICE_MESSAGE));
    assertThat(matcher.matches(request)).isTrue();
  }
}