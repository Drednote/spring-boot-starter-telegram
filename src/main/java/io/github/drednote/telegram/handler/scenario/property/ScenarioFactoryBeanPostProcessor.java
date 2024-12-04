package io.github.drednote.telegram.handler.scenario.property;

import io.github.drednote.telegram.handler.scenario.ActionContext;
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
 * A {@code BeanPostProcessor} that processes beans annotated with {@link TelegramScenario}. It identifies methods
 * annotated with {@link TelegramScenarioAction} and registers them with the {@link ScenarioFactoryContainer}.
 *
 * @author Ivan Galushko
 */
public class ScenarioFactoryBeanPostProcessor implements BeanPostProcessor {

    private final ScenarioFactoryContainer registrar;

    public ScenarioFactoryBeanPostProcessor(ScenarioFactoryContainer registrar) {
        Assert.required(registrar, "ScenarioFactoryContainer");
        this.registrar = registrar;
    }

    /**
     * Processes beans before initialization. For beans annotated with {@link TelegramScenario}, it identifies annotated
     * methods and registers them using the {@link ScenarioFactoryContainer}.
     */
    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
        throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        TelegramScenario scenarioFactory = AnnotationUtils.findAnnotation(targetClass, TelegramScenario.class);
        if (scenarioFactory != null) {
            var annotatedMethods = findAnnotatedMethods(targetClass);
            if (!annotatedMethods.isEmpty()) {
                annotatedMethods.forEach((method, annotation) -> {
                    if (method.getParameterTypes().length == 1
                        && method.getParameterTypes()[0].equals(ActionContext.class)
                    ) {
                        Method invocableMethod = AopUtils.selectInvocableMethod(method, targetClass);
                        registrar.registerAction(bean, invocableMethod, annotation);
                    } else {
                        throw new IllegalStateException(
                            "The method annotated with TelegramScenarioAction must accept exactly one argument of type ActionContext");
                    }
                });
            }
        }
        return bean;
    }

    private Map<Method, TelegramScenarioAction> findAnnotatedMethods(Class<?> targetClass) {
        return MethodIntrospector.selectMethods(targetClass,
            (MethodIntrospector.MetadataLookup<TelegramScenarioAction>) method ->
                AnnotatedElementUtils.findMergedAnnotation(method, TelegramScenarioAction.class));
    }
}
