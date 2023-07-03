package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.updatehandler.mvc.BotControllerBeanPostProcessor;
import com.github.drednote.telegram.updatehandler.mvc.BotControllerContainer;
import com.github.drednote.telegram.updatehandler.mvc.ControllerRegistrar;
import com.github.drednote.telegram.updatehandler.mvc.HandlerMethodPopular;
import com.github.drednote.telegram.updatehandler.mvc.MvcUpdateHandler;
import com.github.drednote.telegram.updatehandler.scenario.Scenario;
import com.github.drednote.telegram.updatehandler.scenario.ScenarioUpdateHandler;
import java.util.Collection;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@EnableConfigurationProperties(UpdateHandlerProperties.class)
public class UpdateHandlerAutoConfiguration {

  @Bean
  @ConditionalOnProperty(
      prefix = "drednote.telegram-bot.update-handler",
      name = "scenario-enabled",
      havingValue = "true",
      matchIfMissing = true
  )
  public UpdateHandler scenarioUpdateHandler(Collection<Scenario> scenarios) {
    return new ScenarioUpdateHandler(scenarios);
  }

  @Configuration
  @ConditionalOnProperty(
      prefix = "drednote.telegram-bot.update-handler",
      name = "mvc-enabled",
      havingValue = "true",
      matchIfMissing = true
  )
  public static class MvcAutoConfiguration {

    @Bean
    public UpdateHandler mvcUpdateHandler(HandlerMethodPopular handlerMethodLookup) {
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
