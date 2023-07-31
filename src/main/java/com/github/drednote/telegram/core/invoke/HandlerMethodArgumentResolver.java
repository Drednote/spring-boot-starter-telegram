package com.github.drednote.telegram.core.invoke;

import com.github.drednote.telegram.core.request.BotRequest;
import org.springframework.core.MethodParameter;

public interface HandlerMethodArgumentResolver {

  Object resolveArgument(MethodParameter parameter, BotRequest request) throws Exception;

  boolean supportsParameter(MethodParameter parameter);
}
