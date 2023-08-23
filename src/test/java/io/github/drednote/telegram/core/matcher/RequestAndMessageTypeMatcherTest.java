package io.github.drednote.telegram.core.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.core.request.RequestType;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RequestAndMessageTypeMatcherTest {

  @Test
  void shouldMatchRequestType() {
    RequestAndMessageTypeMatcher matcher = new RequestAndMessageTypeMatcher(
        new TelegramRequestMapping("", RequestType.POLL, Collections.emptySet()));
    // mock
    TelegramUpdateRequest request = Mockito.mock(TelegramUpdateRequest.class);
    when(request.getMessageTypes()).thenReturn(Collections.emptySet());

    when(request.getRequestType()).thenReturn(RequestType.POLL);
    assertThat(matcher.matches(request)).isTrue();

    when(request.getRequestType()).thenReturn(RequestType.MESSAGE);
    assertThat(matcher.matches(request)).isFalse();

    matcher = new RequestAndMessageTypeMatcher(
        new TelegramRequestMapping("", null, Collections.emptySet()));
    assertThat(matcher.matches(request)).isTrue();
  }

  @Test
  void shouldMatchMessageType() {
    RequestAndMessageTypeMatcher matcher = new RequestAndMessageTypeMatcher(
        new TelegramRequestMapping("", RequestType.MESSAGE, Set.of(MessageType.COMMAND)));
    // mock
    TelegramUpdateRequest request = Mockito.mock(TelegramUpdateRequest.class);
    when(request.getRequestType()).thenReturn(RequestType.MESSAGE);

    when(request.getMessageTypes()).thenReturn(Collections.emptySet());
    assertThat(matcher.matches(request)).isFalse();

    when(request.getMessageTypes()).thenReturn(Set.of(MessageType.COMMAND));
    assertThat(matcher.matches(request)).isTrue();

    matcher = new RequestAndMessageTypeMatcher(
        new TelegramRequestMapping("", RequestType.MESSAGE,
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