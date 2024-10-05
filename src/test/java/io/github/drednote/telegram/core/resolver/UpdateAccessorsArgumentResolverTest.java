package io.github.drednote.telegram.core.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.drednote.telegram.core.TelegramBot;
import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.inlinequery.ChosenInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.ShippingQuery;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

class UpdateAccessorsArgumentResolverTest {

  private final UpdateAccessorsArgumentResolver updateAccessorsArgumentResolver = new UpdateAccessorsArgumentResolver();

  @ParameterizedTest
  @ValueSource(classes = {
      Update.class, Message.class, User.class, Chat.class, InlineQuery.class, Poll.class,
      PollAnswer.class, ChosenInlineQuery.class, CallbackQuery.class, ShippingQuery.class,
      PreCheckoutQuery.class, ChatMemberUpdated.class, ChatJoinRequest.class
  })
  void shouldNotThrowExceptionAndResolve(Class<?> clazz) {
    MethodParameter parameter = Mockito.mock(MethodParameter.class);
    when(((Class) parameter.getParameterType())).thenReturn(clazz);

    DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createMessage("1"));
    request.setError(new Exception());

    assertThat(updateAccessorsArgumentResolver.supportsParameter(parameter)).isTrue();
    assertThat(updateAccessorsArgumentResolver.resolveArgument(parameter, request))
        .getClass().isAssignableFrom(clazz);
  }

  @ParameterizedTest
  @ValueSource(classes = {
      UpdateRequest.class, TelegramBot.class,
      Throwable.class, String.class, Long.class
  })
  void shouldThrowException(Class<?> clazz) {
    MethodParameter parameter = Mockito.mock(MethodParameter.class);
    when(((Class) parameter.getParameterType())).thenReturn(clazz);

    DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(
        UpdateUtils.createMessage("1"));
    request.setError(new Exception());

    assertThat(updateAccessorsArgumentResolver.supportsParameter(parameter)).isFalse();
    assertThatThrownBy(
        () -> updateAccessorsArgumentResolver.resolveArgument(parameter, request))
        .isInstanceOf(IllegalArgumentException.class);
  }

}