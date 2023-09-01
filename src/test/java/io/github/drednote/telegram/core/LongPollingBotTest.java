package io.github.drednote.telegram.core;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.exception.ExceptionHandler;
import io.github.drednote.telegram.filter.UpdateFilterProvider;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.support.builder.UpdateBuilder;
import io.github.drednote.telegram.response.TelegramResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class LongPollingBotTest {

  private LongPollingBot longPollingBot;
  private UpdateHandler updateHandler;
  private ExceptionHandler exceptionHandler;
  private PreUpdateFilter preUpdateFilter;
  private PostUpdateFilter postUpdateFilter;

  @BeforeEach
  void setUp() {
    UpdateFilterProvider filterProvider = Mockito.mock(UpdateFilterProvider.class);
    exceptionHandler = Mockito.mock(ExceptionHandler.class);
    updateHandler = Mockito.mock(UpdateHandler.class);
    preUpdateFilter = Mockito.mock(PreUpdateFilter.class);
    postUpdateFilter = Mockito.mock(PostUpdateFilter.class);
    when(filterProvider.getPreFilters(any())).thenReturn(List.of(preUpdateFilter));
    when(filterProvider.getPostFilters(any())).thenReturn(List.of(postUpdateFilter));
    this.longPollingBot = new LongPollingBot(new TelegramProperties(), List.of(updateHandler),
        new ObjectMapper(), exceptionHandler, filterProvider, new TelegramMessageSource());
  }

  @Test
  void shouldCallFiltersAndNotFailIfErrorInUpdateHandlers() throws Throwable {
    TelegramResponse response = Mockito.mock(TelegramResponse.class);
    doAnswer(invocation -> {
      UpdateRequest request = invocation.getArgument(0, UpdateRequest.class);
      request.setResponse(response);
      throw new Exception();
    }).when(updateHandler).onUpdate(any());

    assertThatNoException().isThrownBy(() -> longPollingBot.onUpdateReceived(
        UpdateBuilder._default("Hello").message()));

    verify(updateHandler).onUpdate(any());
    verify(exceptionHandler).handle(any());
    verify(preUpdateFilter).preFilter(any());
    verify(postUpdateFilter).postFilter(any());
    verify(response, never()).process(any());
  }

  @Test
  void shouldCallFiltersAndNotFailIfErrorInPreFilters() throws Throwable {
    doThrow(new RuntimeException()).when(preUpdateFilter).preFilter(any());

    assertThatNoException().isThrownBy(() -> longPollingBot.onUpdateReceived(
        UpdateBuilder._default("Hello").message()));

    verify(updateHandler, never()).onUpdate(any());
    verify(exceptionHandler).handle(any());
    verify(preUpdateFilter).preFilter(any());
    verify(postUpdateFilter).postFilter(any());
  }

  @Test
  void shouldCallResponse() throws Throwable {
    TelegramResponse response = Mockito.mock(TelegramResponse.class);
    doAnswer(invocation -> {
      UpdateRequest request = invocation.getArgument(0, UpdateRequest.class);
      request.setResponse(response);
      return null;
    }).when(updateHandler).onUpdate(any());

    assertThatNoException().isThrownBy(() -> longPollingBot.onUpdateReceived(
        UpdateBuilder._default("Hello").message()));

    verify(updateHandler).onUpdate(any());
    verify(exceptionHandler, never()).handle(any());
    verify(preUpdateFilter).preFilter(any());
    verify(postUpdateFilter).postFilter(any());
    verify(response).process(any());
  }

  @Test
  void shouldCallPostFiltersAndNotThrowException() throws Throwable {
    TelegramResponse response = Mockito.mock(TelegramResponse.class);
    doThrow(new RuntimeException()).when(postUpdateFilter).postFilter(any());

    assertThatNoException().isThrownBy(() -> longPollingBot.onUpdateReceived(
        UpdateBuilder._default("Hello").message()));

    verify(updateHandler).onUpdate(any());
    verify(exceptionHandler).handle(any());
    verify(preUpdateFilter).preFilter(any());
    verify(postUpdateFilter).postFilter(any());
    verify(response, never()).process(any());
  }

  @Test
  void shouldSaveAndRemoveContext() throws Throwable {
    try (MockedStatic<UpdateRequestContext> mockStatic = Mockito.mockStatic(
        UpdateRequestContext.class)) {
      TelegramResponse response = Mockito.mock(TelegramResponse.class);
      doThrow(new Exception()).when(updateHandler).onUpdate(any());
      doThrow(new RuntimeException()).when(response).process(any());
      doAnswer(invocation -> {
        UpdateRequest request = invocation.getArgument(0, UpdateRequest.class);
        request.setResponse(response);
        return request;
      }).when(exceptionHandler).handle(any());

      assertThatNoException().isThrownBy(() -> longPollingBot.onUpdateReceived(
          UpdateBuilder._default("Hello").message()));

      verify(updateHandler).onUpdate(any());
      verify(exceptionHandler, times(2)).handle(any());
      verify(preUpdateFilter).preFilter(any());
      verify(postUpdateFilter).postFilter(any());
      verify(response).process(any());
      mockStatic.verify(() -> UpdateRequestContext.saveRequest(any()));
      mockStatic.verify(() -> UpdateRequestContext.removeRequest(eq(true)));
    }
  }
}