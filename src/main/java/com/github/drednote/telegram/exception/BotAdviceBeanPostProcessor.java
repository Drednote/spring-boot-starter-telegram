package com.github.drednote.telegram.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class BotAdviceBeanPostProcessor implements BeanPostProcessor {

  private final ExceptionHandlerRegistrar registrar;

  @Override
  public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
      throws BeansException {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    BotAdvice botAdvice = AnnotationUtils.findAnnotation(targetClass, BotAdvice.class);
    if (botAdvice != null) {
      registrar.register(bean, targetClass);
    }
    return bean;
  }
}
