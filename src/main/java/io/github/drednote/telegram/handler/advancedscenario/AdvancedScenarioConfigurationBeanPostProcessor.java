package io.github.drednote.telegram.handler.advancedscenario;

import io.github.drednote.telegram.core.TelegramRequestScope;
import io.github.drednote.telegram.core.annotation.TelegramScope;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenario;
import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenarioController;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;
import lombok.Getter;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class AdvancedScenarioConfigurationBeanPostProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;
    private final List<AdvancedScenarioInfo> scenarios = new CopyOnWriteArrayList<>();

    public AdvancedScenarioConfigurationBeanPostProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName)
            throws BeansException {

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        AdvancedScenarioController annotation = AnnotationUtils.findAnnotation(targetClass, AdvancedScenarioController.class);

        if (annotation != null) {
            validateImplementation(bean, targetClass);
            processScenario(bean, annotation, targetClass);

            // Configure the custom scope if @TelegramScope is present
            setCustomScopeIfPresent(beanName, targetClass);
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

    private void processScenario(Object bean, AdvancedScenarioController annotation, Class<?> targetClass) {
        IAdvancedScenarioConfig configBean = (IAdvancedScenarioConfig) bean;
        scenarios.add(new AdvancedScenarioInfo(
                annotation.name(),
                configBean.getScenario()
        ));
    }

    private void setCustomScopeIfPresent(String beanName, Class<?> targetClass) {
        ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

        // Check if @TelegramScope is present
        if (targetClass.isAnnotationPresent(TelegramScope.class)) {
            beanDefinition.setScope(TelegramRequestScope.BOT_SCOPE_NAME); // Set the custom scope
        }
    }

    public record AdvancedScenarioInfo(String name, AdvancedScenario<?> scenario) {
    }


}
