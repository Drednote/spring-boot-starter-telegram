package io.github.drednote.telegram.handler;

import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.datasource.DataSourceAutoConfiguration;
import io.github.drednote.telegram.filter.pre.ControllerUpdateHandlerPopular;
import io.github.drednote.telegram.handler.controller.ControllerRegistrar;
import io.github.drednote.telegram.handler.controller.ControllerUpdateHandler;
import io.github.drednote.telegram.handler.controller.HandlerMethodPopular;
import io.github.drednote.telegram.handler.controller.TelegramControllerBeanPostProcessor;
import io.github.drednote.telegram.handler.controller.TelegramControllerContainer;
import io.github.drednote.telegram.handler.scenario.ScenarioAutoConfiguration;
import io.github.drednote.telegram.session.SessionProperties;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({UpdateHandlerProperties.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ImportAutoConfiguration(ScenarioAutoConfiguration.class)
public class UpdateHandlerAutoConfiguration {

    public UpdateHandlerAutoConfiguration(
        SessionProperties sessionProperties, UpdateHandlerProperties updateHandlerProperties
    ) {
        if (sessionProperties.getMaxThreadsPerUser() != 1
            && updateHandlerProperties.isScenarioEnabled()
            && updateHandlerProperties.isEnabledWarningForScenario()) {
            String msg = """
                
                
                You enabled scenario and also set the drednote.telegram.session.MaxThreadsPerUser \
                value to be different from 1.
                This is unsafe, since all the scenario code is written in such a way \
                that it implies sequential processing within one user.
                Consider disable the scenario handling, \
                or set drednote.telegram.session.MaxThreadsPerUser to 1.
                
                You can disable this warning by setting drednote.telegram.update-handler.enabledWarningForScenario to false
                
                """;
            throw new BeanCreationException(msg);
        }
    }

    @AutoConfiguration
    @ConditionalOnProperty(
        prefix = "drednote.telegram.update-handler",
        name = "controller-enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    public static class ControllerAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ControllerUpdateHandlerPopular updateHandlerPopular(
            HandlerMethodPopular handlerMethodLookup) {
            return new ControllerUpdateHandlerPopular(handlerMethodLookup);
        }

        @Bean
        @ConditionalOnMissingBean
        public ControllerUpdateHandler mvcUpdateHandler(
            HandlerMethodInvoker handlerMethodInvoker
        ) {
            return new ControllerUpdateHandler(handlerMethodInvoker);
        }

        @Bean
        @ConditionalOnMissingBean({ControllerRegistrar.class, HandlerMethodPopular.class})
        public static TelegramControllerContainer handlerMethodContainer() {
            return new TelegramControllerContainer();
        }

        @Bean
        public static TelegramControllerBeanPostProcessor botControllerBeanPostProcessor(
            ControllerRegistrar registrar
        ) {
            return new TelegramControllerBeanPostProcessor(registrar);
        }
    }
}
