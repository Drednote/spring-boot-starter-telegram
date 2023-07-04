package com.github.drednote.telegram.core;

import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.ShippingQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

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
    } else if (AbsSender.class.isAssignableFrom(paramType)) {
      return request.getAbsSender();
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
        AbsSender.class.isAssignableFrom(paramType) ||
        Long.class.isAssignableFrom(paramType) ||
        Update.class.isAssignableFrom(paramType) ||
        Message.class.isAssignableFrom(paramType) ||
        ShippingQuery.class.isAssignableFrom(paramType) ||
        PreCheckoutQuery.class.isAssignableFrom(paramType) ||
        Chat.class.isAssignableFrom(paramType) ||
        Throwable.class.isAssignableFrom(paramType) ||
        User.class.isAssignableFrom(paramType);
  }
}
