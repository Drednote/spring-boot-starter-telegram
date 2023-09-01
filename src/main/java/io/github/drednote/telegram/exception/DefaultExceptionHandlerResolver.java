package io.github.drednote.telegram.exception;

import io.github.drednote.telegram.core.annotation.TelegramExceptionHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.ExceptionDepthComparator;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils.MethodFilter;
import org.springframework.web.method.HandlerMethod;

/**
 * This class, {@code DefaultExceptionHandlerResolver}, implements the
 * {@link ExceptionHandlerResolver} and {@link ExceptionHandlerRegistrar} interfaces. It is
 * responsible for resolving exception handlers and registering them for specific throwable types
 *
 * <p>The class contains methods to handle and resolve exceptions by associating them with
 * appropriate handler methods
 *
 * @author Ivan Galushko
 */
public class DefaultExceptionHandlerResolver
    implements ExceptionHandlerResolver, ExceptionHandlerRegistrar {

  /**
   * A method filter that identifies handler methods annotated with
   * {@code TelegramExceptionHandler}.
   */
  private static final MethodFilter EXCEPTION_HANDLER_METHODS = method ->
      AnnotatedElementUtils.hasAnnotation(method, TelegramExceptionHandler.class);
  /**
   * A mapping of throwable types to their associated handler methods.
   */
  private final Map<Class<? extends Throwable>, HandlerMethod> exceptionLookup = new HashMap<>();
  /**
   * A cached mapping of throwable types to their associated handler methods.
   */
  private final Map<Class<? extends Throwable>, HandlerMethod> cacheExceptionLookup = new HashMap<>();

  /**
   * Resolves the appropriate {@link HandlerMethod} for the given throwable. In other words resolves
   * method marked with {@link TelegramExceptionHandler}, that handles given throwable
   *
   * @param throwable The throwable for which to resolve the handler
   * @return The resolved handler method, or {@code null} if not found
   */
  @Override
  @Nullable
  public HandlerMethod resolve(Throwable throwable) {
    Assert.notNull(throwable, "Throwable must not be null");
    Class<? extends Throwable> exceptionType = throwable.getClass();
    return this.cacheExceptionLookup.computeIfAbsent(exceptionType,
        key -> findMethod(exceptionType));
  }

  @Nullable
  private HandlerMethod findMethod(Class<? extends Throwable> exceptionType) {
    List<Class<? extends Throwable>> matches = new ArrayList<>();
    for (Class<? extends Throwable> mappedException : this.exceptionLookup.keySet()) {
      if (mappedException.isAssignableFrom(exceptionType)) {
        matches.add(mappedException);
      }
    }
    return matches.stream()
        .min(new ExceptionDepthComparator(exceptionType))
        .map(exceptionLookup::get)
        .orElse(null);
  }

  /**
   * Registers exception handler methods for a specific bean and its target class
   *
   * @param bean        The object containing the handler methods
   * @param targetClass The target class associated with the bean
   */
  @Override
  public void register(Object bean, Class<?> targetClass) {
    Set<Method> methods = MethodIntrospector.selectMethods(targetClass, EXCEPTION_HANDLER_METHODS);
    for (Method method : methods) {
      for (Class<? extends Throwable> exceptionType : detectExceptionMappings(method)) {
        addExceptionMapping(exceptionType, method, bean);
      }
    }
  }

  private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method,
      Object bean) {
    HandlerMethod oldMethod = this.exceptionLookup.put(exceptionType,
        new HandlerMethod(bean, method));
    if (oldMethod != null && !oldMethod.getMethod().equals(method)) {
      throw new IllegalStateException("Ambiguous @TelegramExceptionHandler method mapped for [" +
          exceptionType + "]: {" + oldMethod + ", " + method + "}");
    }
  }

  @SuppressWarnings("unchecked")
  private List<Class<? extends Throwable>> detectExceptionMappings(Method method) {
    List<Class<? extends Throwable>> result = new ArrayList<>();
    detectAnnotationExceptionMappings(method, result);
    if (result.isEmpty()) {
      for (Class<?> paramType : method.getParameterTypes()) {
        if (Throwable.class.isAssignableFrom(paramType)) {
          result.add((Class<? extends Throwable>) paramType);
        }
      }
    }
    if (result.isEmpty()) {
      throw new IllegalStateException("No exception types mapped to " + method);
    }
    return result;
  }

  private void detectAnnotationExceptionMappings(Method method,
      List<Class<? extends Throwable>> result) {
    TelegramExceptionHandler ann = AnnotatedElementUtils.findMergedAnnotation(method,
        TelegramExceptionHandler.class);
    Assert.state(ann != null, "No TelegramExceptionHandler annotation");
    result.addAll(Arrays.asList(ann.value()));
  }
}
