package com.github.drednote.telegram.core;

import org.springframework.core.MethodParameter;

public interface HandlerMethodArgumentResolver {

  Object resolveArgument(MethodParameter parameter, UpdateRequest request) throws Exception;

  boolean supportsParameter(MethodParameter parameter);
}
