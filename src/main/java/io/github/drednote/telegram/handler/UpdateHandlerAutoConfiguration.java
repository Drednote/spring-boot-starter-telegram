package io.github.drednote.telegram.handler;

import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.datasource.DataSourceAutoConfiguration;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.filter.pre.ControllerUpdateHandlerPopular;
import io.github.drednote.telegram.filter.pre.ScenarioUpdateHandlerPopular;
import io.github.drednote.telegram.handler.controller.ControllerRegistrar;
import io.github.drednote.telegram.handler.controller.ControllerUpdateHandler;
import io.github.drednote.telegram.handler.controller.HandlerMethodPopular;
import io.github.drednote.telegram.handler.controller.TelegramControllerBeanPostProcessor;
import io.github.drednote.telegram.handler.controller.TelegramControllerContainer;
import io.github.drednote.telegram.handler.scenario.ScenarioConfig;
import io.github.drednote.telegram.handler.scenario.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties;
import io.github.drednote.telegram.handler.scenario.ScenarioUpdateHandler;
import io.github.drednote.telegram.handler.scenario.SimpleScenarioConfig;
import io.github.drednote.telegram.handler.scenario.SimpleScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder.ScenarioData;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioFactory;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioFactory;
import io.github.drednote.telegram.handler.scenario.persist.SimpleScenarioPersister;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryBeanPostProcessor;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryContainer;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryResolver;
import io.github.drednote.telegram.handler.scenario.property.ScenarioPropertiesConfigurer;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.utils.FieldProvider;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({UpdateHandlerProperties.class, ScenarioProperties.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
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
        name = "scenario-enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    @ConditionalOnBean(ScenarioConfigurerAdapter.class)
    public static class ScenarioAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ScenarioUpdateHandler scenarioUpdateHandler(
        ) {
            return new ScenarioUpdateHandler();
        }

        @Bean
        @ConditionalOnMissingBean
        public ScenarioFactoryBeanPostProcessor scenarioFactoryBeanPostProcessor(ScenarioFactoryContainer container) {
            return new ScenarioFactoryBeanPostProcessor(container);
        }

        @Bean
        @ConditionalOnMissingBean
        public ScenarioFactoryContainer scenarioFactoryContainer() {
            return new ScenarioFactoryContainer();
        }

        @Bean
        @ConditionalOnMissingBean
        public <S> ScenarioUpdateHandlerPopular<S> scenarioUpdateHandlerPopular(
            ScenarioConfigurerAdapter<S> adapter, ScenarioProperties scenarioProperties,
            @Autowired(required = false) ScenarioIdRepositoryAdapter scenarioIdAdapter,
            ScenarioFactoryResolver scenarioFactoryResolver
        ) {
            ScenarioBuilder<S> builder = new ScenarioBuilder<>();
            adapter.onConfigure(new SimpleScenarioStateConfigurer<>(builder));
            adapter.onConfigure(new SimpleScenarioConfigConfigurer<>(builder));
            adapter.onConfigure(new SimpleScenarioTransitionConfigurer<>(builder));
            new ScenarioPropertiesConfigurer(scenarioProperties, scenarioFactoryResolver).configure(builder);
            ScenarioData<S> data = builder.build();

            ScenarioIdResolver resolver = data.resolver() == null
                ? new SimpleScenarioIdResolver(FieldProvider.create(scenarioIdAdapter))
                : data.resolver();

            ScenarioConfig<S> scenarioConfig = new SimpleScenarioConfig<>(
                data.initialState(), data.states(), data.terminalStates(), resolver
            );

            FieldProvider<ScenarioRepositoryAdapter<S>> repositoryAdapter = data.adapter();
            SimpleScenarioPersister<S> persister = new SimpleScenarioPersister<>(repositoryAdapter);
            ScenarioFactory<S> factory = new SimpleScenarioFactory<>(scenarioConfig, persister);
            return new ScenarioUpdateHandlerPopular<>(persister, factory, resolver);
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
