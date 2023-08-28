package io.github.drednote.telegram.exception;

import io.github.drednote.telegram.utils.Assert;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;

/**
 * The {@code TelegramAdviceBeanPostProcessor} class implements the {@code BeanPostProcessor}
 * interface. It is responsible for processing beans with the {@link TelegramAdvice} annotation and
 * registering them with an {@link ExceptionHandlerRegistrar}.
 *
 * <p>This class enhances beans that are marked with the {@code TelegramAdvice} annotation by
 * registering them as exception handlers using the provided registrar
 *
 * @author Ivan Galushko
 * @see TelegramAdvice
 */
public class TelegramAdviceBeanPostProcessor implements BeanPostProcessor {

  /**
   * The registrar responsible for registering exception handler methods
   */
  private final ExceptionHandlerRegistrar registrar;

  /**
   * Constructs a {@code TelegramAdviceBeanPostProcessor} instance with the given
   * {@code ExceptionHandlerRegistrar}
   *
   * @param registrar The registrar for registering exception handler methods. Must not be null
   * @throws IllegalArgumentException If the registrar is null
   */
  public TelegramAdviceBeanPostProcessor(ExceptionHandlerRegistrar registrar) {
    Assert.required(registrar, "ExceptionHandlerRegistrar");
    this.registrar = registrar;
  }

  /**
   * Processes a bean before initialization, checking for the presence of the {@link TelegramAdvice}
   * annotation and registering the bean as an exception handler if the annotation is found
   *
   * @param bean     The bean instance to be processed
   * @param beanName The name of the bean
   * @return The processed bean instance
   * @throws BeansException If an error occurs during bean processing
   */
  @Override
  public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
      throws BeansException {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    var telegramAdvice = AnnotationUtils.findAnnotation(targetClass, TelegramAdvice.class);
    if (telegramAdvice != null) {
      registrar.register(bean, targetClass);
    }
    return bean;
  }
}
