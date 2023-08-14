package io.github.drednote.telegram.core.resolver;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

public interface HandlerMethodArgumentResolver extends Ordered {

  /**
   * Order for parameters marked by an annotation
   */
  int FIRST_ORDER = HIGHEST_PRECEDENCE + 100;
  /**
   * Order for all other types
   */
  int SECOND_ORDER = HIGHEST_PRECEDENCE + 200;
  String UNKNOWN_PARAMETER_EXCEPTION_MESSAGE = "Found unknown parameter %s. "
      + "Consider call supportsParameter method before actually try to resolve parameter";

  Object resolveArgument(@NonNull MethodParameter parameter,
      @NonNull TelegramUpdateRequest request);

  boolean supportsParameter(@NonNull MethodParameter parameter);
}
