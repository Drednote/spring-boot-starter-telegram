package io.github.drednote.telegram.handler;

import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.datasource.DataSourceAutoConfiguration;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.filter.pre.ScenarioUpdateHandlerPopular;
import io.github.drednote.telegram.handler.controller.ControllerRegistrar;
import io.github.drednote.telegram.handler.controller.ControllerUpdateHandler;
import io.github.drednote.telegram.handler.controller.HandlerMethodPopular;
import io.github.drednote.telegram.handler.controller.TelegramControllerBeanPostProcessor;
import io.github.drednote.telegram.handler.controller.TelegramControllerContainer;
import io.github.drednote.telegram.handler.scenario.ScenarioUpdateHandler;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder.ScenarioData;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioFactory;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioFactory;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioPersister;
import io.github.drednote.telegram.utils.FieldProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(UpdateHandlerProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class UpdateHandlerAutoConfiguration {

    @AutoConfiguration
    @ConditionalOnProperty(
        prefix = "drednote.telegram.update-handler",
        name = "scenario-enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    @ConditionalOnBean(ScenarioConfigurerAdapter.class)
    public static class ScenarioAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ScenarioUpdateHandler scenarioUpdateHandler(UpdateHandlerProperties properties) {
            return new ScenarioUpdateHandler(properties.getScenarioLockMs());
        }

        @Bean
        @ConditionalOnMissingBean
        public <S> ScenarioUpdateHandlerPopular<S> scenarioUpdateHandlerPopular(ScenarioConfigurerAdapter<S> adapter) {
            ScenarioBuilder<S> builder = new ScenarioBuilder<>();
            adapter.onConfigure(new SimpleScenarioStateConfigurer<>(builder));
            adapter.onConfigure(new SimpleScenarioTransitionConfigurer<>(builder));
            adapter.onConfigure(new SimpleScenarioConfigConfigurer<>(builder));
            ScenarioData<S> data = builder.build();

            FieldProvider<ScenarioRepositoryAdapter<S>> repositoryAdapter = data.adapter();
            SimpleScenarioPersister<S> persister = new SimpleScenarioPersister<>(repositoryAdapter);
            ScenarioFactory<S> factory = new SimpleScenarioFactory<>(data.scenarioConfig(), persister);
            return new ScenarioUpdateHandlerPopular<>(persister, factory, data.resolver());
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
        public ControllerUpdateHandler mvcUpdateHandler(
            HandlerMethodInvoker handlerMethodInvoker
        ) {
            return new ControllerUpdateHandler(handlerMethodInvoker);
        }

        @Bean
        @ConditionalOnMissingBean({ControllerRegistrar.class, HandlerMethodPopular.class})
        public TelegramControllerContainer handlerMethodContainer() {
            return new TelegramControllerContainer();
        }

        @Bean
        public TelegramControllerBeanPostProcessor botControllerBeanPostProcessor(
            ControllerRegistrar registrar
        ) {
            return new TelegramControllerBeanPostProcessor(registrar);
        }
    }
}
