package io.github.drednote.telegram.core.resolver;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.ChosenInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.ShippingQuery;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

/**
 * The {@code RequestArgumentResolver} class is an implementation of the
 * {@code HandlerMethodArgumentResolver} interface that resolves {@link Update}'s arguments
 *
 * @author Ivan Galushko
 * @see Update
 */
public class UpdateAccessorsArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public Object resolveArgument(MethodParameter parameter, UpdateRequest request) {
    Assert.notNull(parameter, "MethodParameter");
    Assert.notNull(request, "UpdateRequest");

    Class<?> parameterType = parameter.getParameterType();
    if (Update.class.isAssignableFrom(parameterType)) {
      return request.getOrigin();
    } else if (Message.class.isAssignableFrom(parameterType)) {
      return request.getMessage();
    } else if (User.class.isAssignableFrom(parameterType)) {
      return request.getUser();
    } else if (Chat.class.isAssignableFrom(parameterType)) {
      return request.getChat();
    } else if (InlineQuery.class.isAssignableFrom(parameterType)) {
      return request.getOrigin().getInlineQuery();
    } else if (ChosenInlineQuery.class.isAssignableFrom(parameterType)) {
      return request.getOrigin().getChosenInlineQuery();
    } else if (CallbackQuery.class.isAssignableFrom(parameterType)) {
      return request.getOrigin().getCallbackQuery();
    } else if (ShippingQuery.class.isAssignableFrom(parameterType)) {
      return request.getOrigin().getShippingQuery();
    } else if (PreCheckoutQuery.class.isAssignableFrom(parameterType)) {
      return request.getOrigin().getPreCheckoutQuery();
    } else if (Poll.class.isAssignableFrom(parameterType)) {
      return request.getOrigin().getPoll();
    } else if (PollAnswer.class.isAssignableFrom(parameterType)) {
      return request.getOrigin().getPollAnswer();
    } else if (ChatMemberUpdated.class.isAssignableFrom(parameterType)) {
      return request.getOrigin().getChatMember();
    } else if (ChatJoinRequest.class.isAssignableFrom(parameterType)) {
      return request.getOrigin().getChatJoinRequest();
    } else {
      throw new IllegalArgumentException(UNKNOWN_PARAMETER_EXCEPTION_MESSAGE.formatted(parameter));
    }
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Assert.notNull(parameter, "MethodParameter");

    Class<?> paramType = parameter.getParameterType();
    return Update.class.isAssignableFrom(paramType) ||
        Message.class.isAssignableFrom(paramType) ||
        Chat.class.isAssignableFrom(paramType) ||
        User.class.isAssignableFrom(paramType) ||
        InlineQuery.class.isAssignableFrom(paramType) ||
        ChosenInlineQuery.class.isAssignableFrom(paramType) ||
        CallbackQuery.class.isAssignableFrom(paramType) ||
        ShippingQuery.class.isAssignableFrom(paramType) ||
        PreCheckoutQuery.class.isAssignableFrom(paramType) ||
        Poll.class.isAssignableFrom(paramType) ||
        PollAnswer.class.isAssignableFrom(paramType) ||
        ChatMemberUpdated.class.isAssignableFrom(paramType) ||
        ChatJoinRequest.class.isAssignableFrom(paramType);
  }

  @Override
  public int getOrder() {
    return SECOND_ORDER;
  }
}
