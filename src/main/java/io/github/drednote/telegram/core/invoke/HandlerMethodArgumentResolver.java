package io.github.drednote.telegram.core.invoke;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.springframework.core.MethodParameter;

public interface HandlerMethodArgumentResolver {

  Object resolveArgument(MethodParameter parameter, TelegramUpdateRequest request) throws Exception;

  boolean supportsParameter(MethodParameter parameter);
}
