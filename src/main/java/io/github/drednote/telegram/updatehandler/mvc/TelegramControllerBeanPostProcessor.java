package io.github.drednote.telegram.updatehandler.mvc;

import io.github.drednote.telegram.updatehandler.mvc.annotation.TelegramController;
import io.github.drednote.telegram.updatehandler.mvc.annotation.TelegramRequest;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class TelegramControllerBeanPostProcessor implements BeanPostProcessor {

  private final ControllerRegistrar registrar;

  @Override
  public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
      throws BeansException {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    TelegramController telegramController = AnnotationUtils.findAnnotation(targetClass, TelegramController.class);
    if (telegramController != null) {
      var annotatedMethods = findAnnotatedMethodsTelegramRequest(targetClass);
      if (!annotatedMethods.isEmpty()) {
        annotatedMethods.forEach((method, mappingInfoBuilder) -> {
          Method invocableMethod = AopUtils.selectInvocableMethod(method, targetClass);
          mappingInfoBuilder.forEach(
              mappingInfo -> registrar.register(bean, invocableMethod, mappingInfo));
        });
      }
    }
    return bean;
  }

  private Map<Method, TelegramRequestMappingBuilder> findAnnotatedMethodsTelegramRequest(
      Class<?> targetClass) {
    return MethodIntrospector.selectMethods(targetClass,
        (MethodIntrospector.MetadataLookup<TelegramRequestMappingBuilder>) method -> {
          var telegramRequest = AnnotatedElementUtils.findMergedAnnotation(method,
              TelegramRequest.class);
          if (telegramRequest != null) {
            return new TelegramRequestMappingBuilder(telegramRequest);
          } else {
            return null;
          }
        });
  }
}
