package io.github.drednote.telegram.exception;

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

public class DefaultExceptionHandlerResolver
    implements ExceptionHandlerResolver, ExceptionHandlerRegistrar {

  private final Map<Class<? extends Throwable>, HandlerMethod> exceptionLookup = new HashMap<>();
  private final Map<Class<? extends Throwable>, HandlerMethod> cacheExceptionLookup = new HashMap<>();
  public static final MethodFilter EXCEPTION_HANDLER_METHODS = method ->
      AnnotatedElementUtils.hasAnnotation(method, TelegramExceptionHandler.class);

  @Override
  @Nullable
  public HandlerMethod resolve(Throwable throwable) {
    Assert.notNull(throwable, "Throwable must not be null");
    Class<? extends Throwable> exceptionType = throwable.getClass();
    return this.cacheExceptionLookup.computeIfAbsent(exceptionType,
        key -> findMethod(exceptionType));
  }

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
      throw new IllegalStateException("Ambiguous @BotExceptionHandler method mapped for [" +
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
    Assert.state(ann != null, "No BotExceptionHandler annotation");
    result.addAll(Arrays.asList(ann.value()));
  }
}
