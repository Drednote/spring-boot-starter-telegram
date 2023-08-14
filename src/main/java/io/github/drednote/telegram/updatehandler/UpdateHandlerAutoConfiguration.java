package io.github.drednote.telegram.updatehandler;

import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.datasource.DataSourceAdapter;
import io.github.drednote.telegram.datasource.DataSourceAutoConfiguration;
import io.github.drednote.telegram.updatehandler.mvc.ControllerRegistrar;
import io.github.drednote.telegram.updatehandler.mvc.HandlerMethodPopular;
import io.github.drednote.telegram.updatehandler.mvc.MvcUpdateHandler;
import io.github.drednote.telegram.updatehandler.mvc.TelegramControllerBeanPostProcessor;
import io.github.drednote.telegram.updatehandler.mvc.TelegramControllerContainer;
import io.github.drednote.telegram.updatehandler.scenario.DataSourceScenarioPersister;
import io.github.drednote.telegram.updatehandler.scenario.InMemoryScenarioPersister;
import io.github.drednote.telegram.updatehandler.scenario.ScenarioAdapter;
import io.github.drednote.telegram.updatehandler.scenario.ScenarioMachineContainer;
import io.github.drednote.telegram.updatehandler.scenario.ScenarioUpdateHandler;
import io.github.drednote.telegram.updatehandler.scenario.configurer.ScenarioMachineConfigurerImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
  public static class ScenarioAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ScenarioUpdateHandler scenarioUpdateHandler(
        ObjectProvider<ScenarioAdapter> adapters, UpdateHandlerProperties properties,
        ObjectProvider<DataSourceAdapter> dataSourceAdapter
    ) {
      ScenarioMachineConfigurerImpl configurer = new ScenarioMachineConfigurerImpl();
      adapters.forEach(scenarioAdapter -> scenarioAdapter.onConfigure(configurer));

      configureMissedPersister(configurer, dataSourceAdapter, properties);
      return new ScenarioUpdateHandler(new ScenarioMachineContainer(configurer, properties));
    }

    private void configureMissedPersister(
        ScenarioMachineConfigurerImpl configurer,
        ObjectProvider<DataSourceAdapter> dataSourceAdapter, UpdateHandlerProperties properties
    ) {
      if (configurer.getPersister() == null && properties.isAutoConfigureScenarioPersister()) {
        DataSourceAdapter adapter = dataSourceAdapter.getIfAvailable();
        if (adapter != null) {
          configurer.withPersister(new DataSourceScenarioPersister(adapter));
        } else {
          configurer.withPersister(new InMemoryScenarioPersister());
        }
      }
    }
  }

  @AutoConfiguration
  @ConditionalOnProperty(
      prefix = "drednote.telegram.update-handler",
      name = "mvc-enabled",
      havingValue = "true",
      matchIfMissing = true
  )
  public static class MvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MvcUpdateHandler mvcUpdateHandler(
        HandlerMethodPopular handlerMethodLookup, HandlerMethodInvoker handlerMethodInvoker
    ) {
      return new MvcUpdateHandler(handlerMethodLookup, handlerMethodInvoker);
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
