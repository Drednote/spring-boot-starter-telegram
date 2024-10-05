package io.github.drednote.telegram.core.resolver;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import org.springframework.core.MethodParameter;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * The {@code RequestArgumentResolver} class is an implementation of the
 * {@code HandlerMethodArgumentResolver} interface that resolves base arguments like
 * {@link UpdateRequest} or {@link TelegramClient}
 *
 * @author Ivan Galushko
 */
public class RequestArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public Object resolveArgument(MethodParameter parameter, UpdateRequest request) {
    Assert.notNull(parameter, "MethodParameter");
    Assert.notNull(request, "UpdateRequest");

    Class<?> paramType = parameter.getParameterType();
    if (UpdateRequest.class.isAssignableFrom(paramType)) {
      return request;
    } else if (TelegramClient.class.isAssignableFrom(paramType)) {
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
    return UpdateRequest.class.isAssignableFrom(paramType) ||
        TelegramClient.class.isAssignableFrom(paramType) ||
        Long.class.isAssignableFrom(paramType) ||
        String.class.isAssignableFrom(paramType) ||
        Throwable.class.isAssignableFrom(paramType);
  }

  @Override
  public int getOrder() {
    return SECOND_ORDER;
  }
}
