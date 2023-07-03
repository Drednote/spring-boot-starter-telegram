package com.github.drednote.telegram.core;

import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.ShippingQuery;
import org.telegram.telegrambots.meta.generics.TelegramBot;

public class DefaultHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public Object resolveArgument(MethodParameter parameter, UpdateRequest request) {
    Class<?> paramType = parameter.getParameterType();
    if (UpdateRequest.class.isAssignableFrom(paramType)) {
      return new ImmutableUpdateRequest(request);
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
      return request.getBot();
    } else if (Long.class.isAssignableFrom(paramType)) {
      return request.getChatId();
    } else if (ShippingQuery.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getShippingQuery();
    } else if (PreCheckoutQuery.class.isAssignableFrom(paramType)) {
      return request.getOrigin().getPreCheckoutQuery();
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
//        InlineQuery.class.isAssignableFrom(paramType) ||
//        ChosenInlineResult.class.isAssignableFrom(paramType) ||
//        CallbackQuery.class.isAssignableFrom(paramType) ||
//        ShippingQuery.class.isAssignableFrom(paramType) ||
//        PreCheckoutQuery.class.isAssignableFrom(paramType) ||
        Chat.class.isAssignableFrom(paramType) ||
        User.class.isAssignableFrom(paramType);
  }
}
