package io.github.drednote.telegram.core.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public class InvocableHandlerMethod extends HandlerMethod {

  public InvocableHandlerMethod(@NonNull HandlerMethod handlerMethod) {
    super(handlerMethod);
  }

  /**
   * Invoke the handler method with the given argument commands.
   */
  @Nullable
  public Object invoke(Object... args) throws Exception {
    Method method = getBridgedMethod();
    try {
      return method.invoke(getBean(), args);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
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

  @Override
  protected void assertTargetBean(Method method, Object targetBean, @NonNull Object[] args) {
    Class<?> methodDeclaringClass = method.getDeclaringClass();
    Class<?> targetBeanClass = targetBean.getClass();
    if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
      String text = "The mapped handler method class '" + methodDeclaringClass.getName() +
          "' is not an instance of the actual TelegramController bean class '" +
          targetBeanClass.getName() + "'. If the TelegramController requires proxying " +
          "(e.g. due to @Transactional), please use class-based proxying.";
      throw new IllegalStateException(formatInvokeError(text, args));
    }
  }

  @NonNull
  @Override
  protected String formatInvokeError(@NonNull String text, @NonNull Object[] args) {
    return super.formatInvokeError(text, args).replace("Controller", "TelegramController");
  }
}
