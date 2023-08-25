package io.github.drednote.telegram.core.resolver;

import io.github.drednote.telegram.core.request.DefaultTelegramUpdateRequest;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.generics.TelegramBot;

/**
 * The {@code RequestArgumentResolver} class is an implementation of the
 * {@code HandlerMethodArgumentResolver} interface that resolves base arguments like
 * {@link TelegramUpdateRequest} or {@link TelegramBot}
 *
 * @author Galushko Ivan
 */
public class RequestArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public Object resolveArgument(MethodParameter parameter, TelegramUpdateRequest request) {
    Assert.notNull(parameter, "MethodParameter");
    Assert.notNull(request, "TelegramUpdateRequest");

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
  public boolean supportsParameter(MethodParameter parameter) {
    Assert.notNull(parameter, "MethodParameter");

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
