package com.github.drednote.telegram.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.drednote.telegram.core.ResponseSetter;
import com.github.drednote.telegram.testsupport.UpdateUtils;
import com.github.drednote.telegram.core.request.DefaultBotRequest;
import com.github.drednote.telegram.updatehandler.response.CompositeHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.EmptyHandlerResponse;
import com.github.drednote.telegram.updatehandler.response.GenericHandlerResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseSetterTest {

  private final DefaultBotRequest updateRequest = new DefaultBotRequest(UpdateUtils.createCommand(""),
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

    EmptyHandlerResponse invoked = EmptyHandlerResponse.INSTANCE;
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
    ResponseSetter.setResponse(updateRequest, List.of(EmptyHandlerResponse.INSTANCE));
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isInstanceOf(CompositeHandlerResponse.class);

    ResponseSetter.setResponse(updateRequest, List.of(EmptyHandlerResponse.INSTANCE, new Object()));
    assertThat(updateRequest.getResponse())
        .isNotNull()
        .isInstanceOf(GenericHandlerResponse.class);
  }
}

