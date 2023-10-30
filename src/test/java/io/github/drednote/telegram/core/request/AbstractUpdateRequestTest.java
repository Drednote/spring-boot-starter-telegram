package io.github.drednote.telegram.core.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.handler.controller.RequestHandler;
import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.handler.scenario.Scenario;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

class AbstractUpdateRequestTest {

  @ParameterizedTest
  @ArgumentsSource(UpdateRequestArgumentsProvider.class)
  void shouldCorrectParseUpdate(Data data) {
    var request = new TestUpdateRequest(data.update);

    assertThat(request.getRequestType()).isEqualTo(data.requestType);
    assertThat(request.getMessageTypes()).containsExactlyElementsOf(data.messageTypes);
    assertThat(request.getMessage() == null).isEqualTo(data.isMessageNull);
    assertThat(request.getUser() == null).isEqualTo(data.isUserNull);
    assertThat(request.getChat() == null).isEqualTo(data.isChatNull);
  }

  @ParameterizedTest
  @ArgumentsSource(UpdateRequestArgumentsProvider.class)
  void shouldCorrectParseMessage(Data data) {
    var request = new TestUpdateRequest(data.update);

    assertThat(request.getRequestType()).isEqualTo(data.requestType);
    assertThat(request.getMessageTypes()).containsExactlyElementsOf(data.messageTypes);
    assertThat(request.getMessage() == null).isEqualTo(data.isMessageNull);
    assertThat(request.getUser() == null).isEqualTo(data.isUserNull);
    assertThat(request.getChat() == null).isEqualTo(data.isChatNull);
  }

  record Data(
      Update update, RequestType requestType, List<MessageType> messageTypes, @Nullable String text,
      boolean isMessageNull, boolean isUserNull, boolean isChatNull
  ) {

    public static Data createData(
        Update update, RequestType requestType, List<MessageType> messageTypes, @Nullable String text,
        boolean isMessageNull, boolean isUserNull, boolean isChatNull
    ) {
      return new Data(
          update, requestType, messageTypes, text, isMessageNull, isUserNull, isChatNull
      );
    }

    public static Data createData(
        Update update, RequestType requestType, MessageType messageType, @Nullable String text,
        boolean isMessageNull, boolean isUserNull, boolean isChatNull
    ) {
      return createData(
          update, requestType, List.of(messageType), text, isMessageNull, isUserNull, isChatNull
      );
    }
  }

  static class TestUpdateRequest extends AbstractUpdateRequest {

    public TestUpdateRequest(Update update) {
      super(update);
    }

    @NonNull
    @Override
    public AbsSender getAbsSender() {
      return null;
    }

    @Nullable
    @Override
    public Permission getPermission() {
      return null;
    }

    @Nullable
    @Override
    public Scenario getScenario() {
      return null;
    }

    @Nullable
    @Override
    public TelegramResponse getResponse() {
      return null;
    }

    @Nullable
    @Override
    public Throwable getError() {
      return null;
    }

    @NonNull
    @Override
    public TelegramProperties getProperties() {
      return null;
    }

    @Nullable
    @Override
    public RequestHandler getRequestHandler() {
      return null;
    }

    @NonNull
    @Override
    public ObjectMapper getObjectMapper() {
      return null;
    }

    @Override
    public void setScenario(@Nullable Scenario scenario) {

    }

    @Override
    public void setResponse(@Nullable TelegramResponse response) {

    }

    @Override
    public void setRequestHandler(@Nullable RequestHandler requestHandler) {

    }

    @Override
    public void setPermission(@Nullable Permission permission) {

    }
  }
}

