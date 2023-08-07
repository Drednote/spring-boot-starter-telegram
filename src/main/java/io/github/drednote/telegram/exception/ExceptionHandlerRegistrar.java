package io.github.drednote.telegram.exception;

public interface ExceptionHandlerRegistrar {

  void register(Object bean, Class<?> targetClass);
}
