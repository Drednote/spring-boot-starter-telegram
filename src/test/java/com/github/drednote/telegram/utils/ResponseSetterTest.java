package com.github.drednote.telegram.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.drednote.telegram.UpdateUtils;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.response.CompositeHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.EmptyHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.GenericHandlerResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseSetterTest {

  private final UpdateRequest updateRequest = new UpdateRequest(UpdateUtils.createCommand(""),
      null, null);

  @BeforeEach
  void setUp() {
    updateRequest.setResponse(null);
  }

  @Test
  void shouldCorrectHandleOne() {
    ResponseSetter.setResponse(updateRequest, null);
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isInstanceOf(EmptyHandlerResponse.class);

    EmptyHandlerResponse invoked = new EmptyHandlerResponse();
    ResponseSetter.setResponse(updateRequest, invoked);
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isEqualTo(invoked);

    ResponseSetter.setResponse(updateRequest, new Object());
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isInstanceOf(GenericHandlerResponse.class);
  }

  @Test
  void shouldCorrectHandleList() {
    ResponseSetter.setResponse(updateRequest, List.of(new EmptyHandlerResponse()));
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isInstanceOf(CompositeHandlerResponse.class);

    ResponseSetter.setResponse(updateRequest, List.of(new EmptyHandlerResponse(), new Object()));
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isInstanceOf(GenericHandlerResponse.class);
  }
}

