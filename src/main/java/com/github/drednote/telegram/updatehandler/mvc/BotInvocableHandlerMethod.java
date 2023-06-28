package com.github.drednote.telegram.updatehandler.mvc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public class BotInvocableHandlerMethod extends HandlerMethod {

  public BotInvocableHandlerMethod(HandlerMethod handlerMethod) {
    super(handlerMethod);
  }

  /**
   * Invoke the handler method with the given argument values.
   */
  @Nullable
  protected Object invoke(Object... args) throws Exception {
    Method method = getBridgedMethod();
    try {
      return method.invoke(getBean(), args);
    } catch (IllegalArgumentException ex) {
      assertTargetBean(method, getBean(), args);
      String text = (ex.getMessage() == null || ex.getCause() instanceof NullPointerException) ?
          "Illegal argument" : ex.getMessage();
      throw new IllegalStateException(formatInvokeError(text, args), ex);
    } catch (InvocationTargetException ex) {
      // Unwrap for HandlerExceptionResolvers ...
      Throwable targetException = ex.getCause();
      if (targetException instanceof RuntimeException runtimeException) {
        throw runtimeException;
      } else if (targetException instanceof Error error) {
        throw error;
      } else if (targetException instanceof Exception exception) {
        throw exception;
      } else {
        throw new IllegalStateException(formatInvokeError("Invocation failure", args),
            targetException);
      }
    }
  }
}
