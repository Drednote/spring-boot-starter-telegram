package io.github.drednote.telegram.core.resolver;

import io.github.drednote.telegram.core.request.DefaultTelegramUpdateRequest;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.generics.TelegramBot;

public class RequestArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public Object resolveArgument(@NonNull MethodParameter parameter,
      @NonNull TelegramUpdateRequest request) {
    Class<?> paramType = parameter.getParameterType();
    if (TelegramUpdateRequest.class.isAssignableFrom(paramType)) {
      return new DefaultTelegramUpdateRequest(request);
    } else if (TelegramBot.class.isAssignableFrom(paramType)) {
      return request.getAbsSender();
    } else if (Throwable.class.isAssignableFrom(paramType)) {
      return request.getError();
    } else if (String.class.isAssignableFrom(paramType)) {
      return request.getText();
    } else if (Long.class.isAssignableFrom(paramType)) {
      return request.getChatId();
    } else {
      throw new IllegalArgumentException(UNKNOWN_PARAMETER_EXCEPTION_MESSAGE.formatted(parameter));
    }
  }

  @Override
  public boolean supportsParameter(@NonNull MethodParameter parameter) {
    Class<?> paramType = parameter.getParameterType();
    return TelegramUpdateRequest.class.isAssignableFrom(paramType) ||
        TelegramBot.class.isAssignableFrom(paramType) ||
        Long.class.isAssignableFrom(paramType) ||
        String.class.isAssignableFrom(paramType) ||
        Throwable.class.isAssignableFrom(paramType);
  }

  @Override
  public int getOrder() {
    return SECOND_ORDER;
  }
}
