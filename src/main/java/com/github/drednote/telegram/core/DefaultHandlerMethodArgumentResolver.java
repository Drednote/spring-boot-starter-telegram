package com.github.drednote.telegram.core;

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
import org.telegram.telegrambots.meta.generics.TelegramBot;

public class DefaultHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public Object resolveArgument(MethodParameter parameter, UpdateRequest request) {
    Class<?> paramType = parameter.getParameterType();
    if (UpdateRequest.class.isAssignableFrom(paramType)) {
      return new ImmutableUpdateRequest(request);
    } else if (Throwable.class.isAssignableFrom(paramType)) {
      return request.getError();
    } else if (Update.class.isAssignableFrom(paramType)) {
      return request.getOrigin();
    } else if (Message.class.isAssignableFrom(paramType)) {
      return request.getMessage();
    } else if (User.class.isAssignableFrom(paramType)) {
      return request.getUser();
    } else if (Chat.class.isAssignableFrom(paramType)) {
      return request.getChat();
    } else if (String.class.isAssignableFrom(paramType)) {
      return request.getText();
    } else if (TelegramBot.class.isAssignableFrom(paramType)) {
      return request.getAbsSender();
    } else if (Long.class.isAssignableFrom(paramType)) {
      return request.getChatId();
    } else if (InlineQuery.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getInlineQuery();
    } else if (ChosenInlineQuery.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getChosenInlineQuery();
    } else if (CallbackQuery.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getCallbackQuery();
    } else if (ShippingQuery.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getShippingQuery();
    } else if (PreCheckoutQuery.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getPreCheckoutQuery();
    } else if (Poll.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getPoll();
    } else if (PollAnswer.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getPollAnswer();
    } else if (ChatMemberUpdated.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getChatMember();
    } else if (ChatJoinRequest.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getChatJoinRequest();
    } else {
      return null;
    }
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Class<?> paramType = parameter.getParameterType();
    return UpdateRequest.class.isAssignableFrom(paramType) ||
        TelegramBot.class.isAssignableFrom(paramType) ||
        Long.class.isAssignableFrom(paramType) ||
        String.class.isAssignableFrom(paramType) ||
        Update.class.isAssignableFrom(paramType) ||
        Message.class.isAssignableFrom(paramType) ||
        Chat.class.isAssignableFrom(paramType) ||
        User.class.isAssignableFrom(paramType) ||
        Throwable.class.isAssignableFrom(paramType) ||
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
}