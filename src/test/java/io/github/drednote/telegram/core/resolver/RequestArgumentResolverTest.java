package io.github.drednote.telegram.core.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

class RequestArgumentResolverTest {

  private final RequestArgumentResolver requestArgumentResolver = new RequestArgumentResolver();

  @ParameterizedTest
  @ValueSource(classes = {
      UpdateRequest.class, TelegramClient.class,
      Throwable.class, String.class, Long.class
  })
  void shouldNotThrowExceptionAndResolve(Class<?> clazz) {
    MethodParameter parameter = Mockito.mock(MethodParameter.class);
    when(((Class) parameter.getParameterType())).thenReturn(clazz);

    DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createMessage("1"));
    request.setError(new Exception());

    assertThat(requestArgumentResolver.supportsParameter(parameter)).isTrue();
    assertThat(requestArgumentResolver.resolveArgument(parameter, request))
        .getClass().isAssignableFrom(clazz);
  }

  @ParameterizedTest
  @ValueSource(classes = {
      Object.class, Update.class,
  })
  void shouldThrowException(Class<?> clazz) {
    MethodParameter parameter = Mockito.mock(MethodParameter.class);
    when(((Class) parameter.getParameterType())).thenReturn(clazz);

    DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createMessage("1"));
    request.setError(new Exception());

    assertThat(requestArgumentResolver.supportsParameter(parameter)).isFalse();
    assertThatThrownBy(
        () -> requestArgumentResolver.resolveArgument(parameter, request))
        .isInstanceOf(IllegalArgumentException.class);
  }
}

