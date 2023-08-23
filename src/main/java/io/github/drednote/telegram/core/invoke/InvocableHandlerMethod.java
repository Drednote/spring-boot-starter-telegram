package io.github.drednote.telegram.core.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

/**
 * The {@code InvocableHandlerMethod} class is a subclass of {@link HandlerMethod} that represents
 * an invocable handler method. It provides a method to invoke the handler method with the given
 * method arguments
 */
public class InvocableHandlerMethod extends HandlerMethod {

  /**
   * Creates a new instance of the {@code InvocableHandlerMethod} class with the given handler
   * method.
   *
   * @param handlerMethod the handler method to invoke, not null
   */
  public InvocableHandlerMethod(HandlerMethod handlerMethod) {
    super(handlerMethod);
  }

  /**
   * Invokes the handler method with the given method arguments
   *
   * @param args the method arguments to pass to the handler method, not null
   * @return the result of invoking the handler method, or null if no result is returned
   * @throws Exception if an error occurs during invocation
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
  protected void assertTargetBean(Method method, Object targetBean, Object[] args) {
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
  protected String formatInvokeError(String text, Object[] args) {
    return super.formatInvokeError(text, args).replace("Controller", "TelegramController");
  }
}
