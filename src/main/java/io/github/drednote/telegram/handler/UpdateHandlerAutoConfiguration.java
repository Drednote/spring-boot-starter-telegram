package io.github.drednote.telegram.handler;

import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.datasource.DataSourceAutoConfiguration;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.controller.ControllerRegistrar;
import io.github.drednote.telegram.handler.controller.ControllerUpdateHandler;
import io.github.drednote.telegram.handler.controller.CompositeRequestValidator;
import io.github.drednote.telegram.handler.controller.HandlerMethodPopular;
import io.github.drednote.telegram.handler.controller.RequestValidator;
import io.github.drednote.telegram.handler.controller.TelegramControllerBeanPostProcessor;
import io.github.drednote.telegram.handler.controller.TelegramControllerContainer;
import io.github.drednote.telegram.handler.scenario.DefaultScenarioPersister;
import io.github.drednote.telegram.handler.scenario.ScenarioAdapter;
import io.github.drednote.telegram.handler.scenario.ScenarioMachineContainer;
import io.github.drednote.telegram.handler.scenario.ScenarioUpdateHandler;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioMachineConfigurerImpl;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

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
  public static class ScenarioAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ScenarioUpdateHandler scenarioUpdateHandler(
        ObjectProvider<ScenarioAdapter> adapters, UpdateHandlerProperties properties,
        ScenarioRepositoryAdapter scenarioRepositoryAdapter
    ) {
      ScenarioMachineConfigurerImpl configurer = new ScenarioMachineConfigurerImpl();
      adapters.forEach(scenarioAdapter -> scenarioAdapter.onConfigure(configurer));

      configureMissedPersister(configurer, scenarioRepositoryAdapter, properties);
      return new ScenarioUpdateHandler(new ScenarioMachineContainer(configurer, properties));
    }

    private void configureMissedPersister(
        ScenarioMachineConfigurerImpl configurer,
        ScenarioRepositoryAdapter scenarioRepositoryAdapter, UpdateHandlerProperties properties
    ) {
      if (configurer.getPersister() == null && properties.isAutoConfigureScenarioPersister()) {
        configurer.withPersister(new DefaultScenarioPersister(scenarioRepositoryAdapter));
      }
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
        HandlerMethodPopular handlerMethodLookup, HandlerMethodInvoker handlerMethodInvoker,
        RequestValidator validator
    ) {
      return new ControllerUpdateHandler(handlerMethodLookup, handlerMethodInvoker, validator);
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

    @Bean
    @Primary
    public RequestValidator compositeRequestValidator(List<RequestValidator> validators) {
      return new CompositeRequestValidator(validators);
    }
  }
}
