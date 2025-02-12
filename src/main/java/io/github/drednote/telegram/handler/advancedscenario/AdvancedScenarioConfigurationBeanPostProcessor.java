package io.github.drednote.telegram.handler.advancedscenario;

import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenarioController;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;
import lombok.Getter;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class AdvancedScenarioConfigurationBeanPostProcessor implements BeanPostProcessor {

    private final List<AdvancedScenarioInfo> scenarios = new CopyOnWriteArrayList<>();

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName)
            throws BeansException {

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);

        AdvancedScenarioController annotation =
                targetClass.getAnnotation(AdvancedScenarioController.class);

        if (annotation != null) {
            validateImplementation(bean, targetClass);
            processScenario(bean, annotation, targetClass);
        }
        return bean;
    }

    private void validateImplementation(Object bean, Class<?> targetClass) {
        if (!(bean instanceof IAdvancedScenarioConfig)) {
            throw new IllegalStateException(
                    "Class " + targetClass.getName() +
                            " with @AdvancedScenarioController must implement IAdvancedScenarioConfig"
            );
        }
    }

    private void processScenario(Object bean, AdvancedScenarioController annotation,
                                 Class<?> targetClass) {
        IAdvancedScenarioConfig configBean = (IAdvancedScenarioConfig) bean;
        scenarios.add(new AdvancedScenarioInfo(
                annotation.name(),
                configBean.getScenario()
        ));
    }

    public record AdvancedScenarioInfo(String name, AdvancedScenario scenario) {
    }
}
