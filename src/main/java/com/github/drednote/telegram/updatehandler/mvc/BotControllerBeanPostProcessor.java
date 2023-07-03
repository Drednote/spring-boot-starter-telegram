package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.RequestType;
import com.github.drednote.telegram.updatehandler.mvc.annotation.BotController;
import com.github.drednote.telegram.updatehandler.mvc.annotation.BotRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.EqualsAndHashCode;
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
          mappingInfo.forEach(
              (pattern, type) -> registrar.register(bean, invocableMethod,
                  new BotRequestMappingInfo(pattern, type)
              ));
        });
      }
    }
    return bean;
  }

  private Map<Method, BotRequestInfo> findAnnotatedMethodsBotRequest(Class<?> targetClass) {
    return MethodIntrospector.selectMethods(targetClass,
        (MethodIntrospector.MetadataLookup<BotRequestInfo>) method -> {
          var botRequest = AnnotatedElementUtils.findMergedAnnotation(method, BotRequest.class);
          if (botRequest != null) {
            String[] path = botRequest.path();
            return new BotRequestInfo(arrayOrEmpty(path), arrayOrEmpty(botRequest.type()));
          } else {
            return null;
          }
        });
  }

  @SuppressWarnings("unchecked")
  private <T> T[] arrayOrEmpty(T[] array) {
    return array == null ? (T[]) new Object[0] : array;
  }

  @EqualsAndHashCode
  static final class BotRequestInfo {

    private final String[] patterns;
    private final RequestType[] types;

    BotRequestInfo(String[] patterns, RequestType[] types) {
      this.patterns = patterns;
      this.types = types;
    }

    public void forEach(BiConsumer<String, RequestType> biConsumer) {
      if (types.length > 0) {
        for (RequestType type : types) {
          executeForEach(biConsumer, type);
        }
      } else {
        executeForEach(biConsumer, null);
      }
    }

    private void executeForEach(BiConsumer<String, RequestType> biConsumer, RequestType type) {
      if (patterns.length > 0) {
        for (String pattern : patterns) {
          biConsumer.accept(pattern, type);
        }
      } else {
        biConsumer.accept(type == RequestType.COMMAND ? "/**" : "**", type);
      }
    }
  }
}
