package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.updatehandler.mvc.BotControllerBeanPostProcessor;
import com.github.drednote.telegram.updatehandler.mvc.BotControllerContainer;
import com.github.drednote.telegram.updatehandler.mvc.ControllerRegistrar;
import com.github.drednote.telegram.updatehandler.mvc.HandlerMethodPopular;
import com.github.drednote.telegram.updatehandler.mvc.MvcUpdateHandler;
import com.github.drednote.telegram.updatehandler.scenario.ScenarioAdapter;
import com.github.drednote.telegram.updatehandler.scenario.ScenarioMachineContainer;
import com.github.drednote.telegram.updatehandler.scenario.ScenarioUpdateHandler;
import com.github.drednote.telegram.updatehandler.scenario.configurer.ScenarioMachineConfigurerImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(UpdateHandlerProperties.class)
public class UpdateHandlerAutoConfiguration {

  @AutoConfiguration
  @ConditionalOnProperty(
      prefix = "drednote.telegram-bot.update-handler",
      name = "scenario-enabled",
      havingValue = "true",
      matchIfMissing = true
  )
  public static class ScenarioAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ScenarioUpdateHandler scenarioUpdateHandler(
        ObjectProvider<ScenarioAdapter> adapters, UpdateHandlerProperties properties
    ) {
      ScenarioMachineConfigurerImpl configurer = new ScenarioMachineConfigurerImpl();
      adapters.forEach(scenarioAdapter -> scenarioAdapter.onConfigure(configurer));
      ScenarioMachineContainer container = new ScenarioMachineContainer(configurer, properties);
      return new ScenarioUpdateHandler(container);
    }
  }

  @AutoConfiguration
  @ConditionalOnProperty(
      prefix = "drednote.telegram-bot.update-handler",
      name = "mvc-enabled",
      havingValue = "true",
      matchIfMissing = true
  )
  public static class MvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MvcUpdateHandler mvcUpdateHandler(HandlerMethodPopular handlerMethodLookup) {
      return new MvcUpdateHandler(handlerMethodLookup);
    }

    @Bean
    public BotControllerContainer handlerMethodContainer() {
      return new BotControllerContainer();
    }

    @Bean
    public BotControllerBeanPostProcessor botControllerBeanPostProcessor(
        ControllerRegistrar registrar
    ) {
      return new BotControllerBeanPostProcessor(registrar);
    }
  }
}
