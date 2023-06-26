package com.github.drednote.telegram.updatehandler.mvc;

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
public class BotControllerBeanPostProcessor implements BeanPostProcessor {

  private final ControllerRegistrar registrar;

  @Override
  public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
      throws BeansException {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    BotController botController = AnnotationUtils.findAnnotation(targetClass, BotController.class);
    if (botController != null) {
      var annotatedMethods = findAnnotatedMethodsBotRequest(targetClass);
      if (!annotatedMethods.isEmpty()) {
        annotatedMethods.forEach((method, mappingInfo) -> {
          Method invocableMethod = AopUtils.selectInvocableMethod(method, targetClass);
          registrar.register(bean, invocableMethod, mappingInfo);
        });
      }
    }
    return bean;
  }

  private Map<Method, BotRequestMappingInfo> findAnnotatedMethodsBotRequest(Class<?> targetClass) {
    return MethodIntrospector.selectMethods(targetClass,
        (MethodIntrospector.MetadataLookup<BotRequestMappingInfo>) method -> {
          var botRequest = AnnotatedElementUtils.findMergedAnnotation(method, BotRequest.class);
          return botRequest != null ? BotRequestMappingInfo.newBuilder()
              .path(botRequest.path())
              .build() : null;
        });
  }
}
