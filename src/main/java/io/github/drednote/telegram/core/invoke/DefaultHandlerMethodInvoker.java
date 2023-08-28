package io.github.drednote.telegram.core.invoke;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.core.resolver.HandlerMethodArgumentResolver;
import io.github.drednote.telegram.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

/**
 * The {@code DefaultHandlerMethodInvoker} class is an implementation of the
 * {@link HandlerMethodInvoker} interface that is responsible for invoking handler methods with the
 * given request. It uses a {@link HandlerMethodArgumentResolver} to resolve method arguments and
 * invoke the handler method
 *
 * @author Ivan Galushko
 */
public class DefaultHandlerMethodInvoker implements HandlerMethodInvoker {

  private static final Object[] EMPTY_ARGS = new Object[0];
  private static final Logger log = LoggerFactory.getLogger(DefaultHandlerMethodInvoker.class);

  /**
   * The handler method argument resolver to use
   */
  private final HandlerMethodArgumentResolver resolver;
  /**
   * The {@code ParameterNameDiscoverer} using for method parameter names
   */
  private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

  /**
   * Creates a new instance of the {@code DefaultHandlerMethodInvoker} class with the given handler
   * method argument resolver
   *
   * @param resolver the handler method argument resolver, not null
   */
  public DefaultHandlerMethodInvoker(HandlerMethodArgumentResolver resolver) {
    Assert.required(resolver, "HandlerMethodArgumentResolver");

    this.resolver = resolver;
  }

  /**
   * Invokes the given handler method with the provided request
   *
   * @param request       the update request, not null
   * @param handlerMethod the handler method to invoke, not null
   * @return the result of invoking the handler method, or null if no result is returned
   * @throws Exception if an error occurs during invocation
   */
  @Override
  @Nullable
  public Object invoke(
      TelegramUpdateRequest request, HandlerMethod handlerMethod,
      Object... providedArgs
  ) throws Exception {
    Assert.notNull(request, "TelegramUpdateRequest");
    Assert.notNull(handlerMethod, "HandlerMethod");

    Object[] argumentValues = getMethodArgumentValues(request, handlerMethod, providedArgs);
    if (handlerMethod instanceof InvocableHandlerMethod invocableHandlerMethod) {
      return invocableHandlerMethod.invoke(argumentValues);
    }
    return new InvocableHandlerMethod(handlerMethod).invoke(argumentValues);
  }

  /**
   * Returns the method argument values for the given request and handler method
   *
   * @param request       the update request, not null
   * @param handlerMethod the handler method, not null
   * @param providedArgs  additional provided arguments
   * @return the method argument values
   */
  private Object[] getMethodArgumentValues(
      TelegramUpdateRequest request, HandlerMethod handlerMethod, Object... providedArgs
  ) {
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
  private static Object findProvidedArgument(
      MethodParameter parameter, @Nullable Object... providedArgs
  ) {
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
