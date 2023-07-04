package com.github.drednote.telegram.core;

import com.github.drednote.telegram.updatehandler.mvc.BotInvocableHandlerMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

@Slf4j
public class DefaultHandlerMethodInvoker implements HandlerMethodInvoker {

  private static final Object[] EMPTY_ARGS = new Object[0];

  private final HandlerMethodArgumentResolver resolver = new DefaultHandlerMethodArgumentResolver();
  private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

  @Override
  @Nullable
  public Object invoke(UpdateRequest updateRequest) throws Exception {
    HandlerMethod handlerMethod = updateRequest.getHandlerMethod();
    Object[] argumentValues = getMethodArgumentValues(updateRequest);
    return new BotInvocableHandlerMethod(handlerMethod).invoke(argumentValues);
  }

  private Object[] getMethodArgumentValues(UpdateRequest request, Object... providedArgs) {
    HandlerMethod handlerMethod = request.getHandlerMethod();
    MethodParameter[] parameters = handlerMethod.getMethodParameters();
    if (ObjectUtils.isEmpty(parameters)) {
      return EMPTY_ARGS;
    }

    Object[] args = new Object[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      MethodParameter parameter = parameters[i];
      parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
      args[i] = findProvidedArgument(parameter, providedArgs);
      if (args[i] != null) {
        continue;
      }
      if (!this.resolver.supportsParameter(parameter)) {
        throw new IllegalStateException(formatArgumentError(parameter, "No suitable resolver"));
      }
      try {
        args[i] = this.resolver.resolveArgument(parameter, request);
      } catch (Exception ex) {
        // Leave stack trace for later, exception may actually be resolved and handled...
        if (log.isDebugEnabled()) {
          String exMsg = ex.getMessage();
          if (exMsg != null && !exMsg.contains(parameter.getExecutable().toGenericString())) {
            log.debug(formatArgumentError(parameter, exMsg));
          }
        }
        throw ex;
      }
    }
    return args;
  }

  @Nullable
  private static Object findProvidedArgument(MethodParameter parameter,
      @Nullable Object... providedArgs) {
    if (!ObjectUtils.isEmpty(providedArgs)) {
      for (Object providedArg : providedArgs) {
        if (parameter.getParameterType().isInstance(providedArg)) {
          return providedArg;
        }
      }
    }
    return null;
  }

  private static String formatArgumentError(MethodParameter param, String message) {
    return "Could not resolve parameter [" + param.getParameterIndex() + "] in " +
        param.getExecutable().toGenericString() + (StringUtils.hasText(message) ? ": " + message
        : "");
  }
}
