package io.github.drednote.telegram.updatehandler.controller;

import io.github.drednote.telegram.utils.Assert;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;

/**
 * A {@code BeanPostProcessor} that processes beans annotated with {@link TelegramController}. It
 * identifies methods annotated with {@link TelegramRequest} and registers them with the {@link
 * ControllerRegistrar}.
 * <p>
 * This post-processor is responsible for identifying and registering methods in Telegram
 * controllers that should handle incoming requests.
 *
 * @author Ivan Galushko
 * @see TelegramController
 * @see TelegramRequest
 * @see ControllerRegistrar
 */
public class TelegramControllerBeanPostProcessor implements BeanPostProcessor {

  private final ControllerRegistrar registrar;

  public TelegramControllerBeanPostProcessor(ControllerRegistrar registrar) {
    Assert.required(registrar, "ControllerRegistrar");
    this.registrar = registrar;
  }

  /**
   * Processes beans before initialization. For beans annotated with {@link TelegramController}, it
   * identifies annotated methods and registers them using the {@link ControllerRegistrar}.
   */
  @Override
  public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
      throws BeansException {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    TelegramController telegramController = AnnotationUtils.findAnnotation(targetClass,
        TelegramController.class);
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
