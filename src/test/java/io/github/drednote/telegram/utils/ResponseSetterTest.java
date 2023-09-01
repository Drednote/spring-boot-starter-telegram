package io.github.drednote.telegram.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.EmptyTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseSetterTest {

  private final DefaultUpdateRequest updateRequest = new DefaultUpdateRequest(
      UpdateRequestUtils.createMockRequest(UpdateUtils.createCommand("")));

  @BeforeEach
  void setUp() {
    updateRequest.setResponse(null);
  }

  @Test
  void shouldCorrectHandleOne() {
    ResponseSetter.setResponse(updateRequest, null);
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isInstanceOf(EmptyTelegramResponse.class);

    EmptyTelegramResponse invoked = EmptyTelegramResponse.INSTANCE;
    ResponseSetter.setResponse(updateRequest, invoked);
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isEqualTo(invoked);

    ResponseSetter.setResponse(updateRequest, new Object());
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isInstanceOf(GenericTelegramResponse.class);
  }

  @Test
  void shouldCorrectHandleList() {
    ResponseSetter.setResponse(updateRequest, List.of(EmptyTelegramResponse.INSTANCE));
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isInstanceOf(CompositeTelegramResponse.class);

    ResponseSetter.setResponse(updateRequest,
        List.of(EmptyTelegramResponse.INSTANCE, new Object()));
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isInstanceOf(GenericTelegramResponse.class);
  }
}

