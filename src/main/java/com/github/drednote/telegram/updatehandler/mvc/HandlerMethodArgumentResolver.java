package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.UpdateRequest;
import org.springframework.core.MethodParameter;

public interface HandlerMethodArgumentResolver {

  Object resolveArgument(MethodParameter parameter, UpdateRequest request);

  boolean supportsParameter(MethodParameter parameter);
}
