package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.updatehandler.mvc.BotControllerBeanPostProcessor;
import com.github.drednote.telegram.updatehandler.mvc.ControllerRegistrar;
import com.github.drednote.telegram.updatehandler.mvc.BotControllerContainer;
import com.github.drednote.telegram.updatehandler.mvc.HandlerMethodPopular;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(UpdateHandlerProperties.class)
public class UpdateHandlerAutoConfiguration {

  @Bean
  @ConditionalOnProperty(
      prefix = "drednote.telegram-bot.update-handler",
      name = "type",
      havingValue = "logging",
      matchIfMissing = true
  )
  @ConditionalOnMissingBean
  public UpdateHandler loggingUpdateHandler() {
    return new LoggingUpdateHandler();
  }

  @ConditionalOnProperty(
      prefix = "drednote.telegram-bot.update-handler",
      name = "type",
      havingValue = "mvc"
  )
  @AutoConfiguration
  public static class Mvc {

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

    @Bean
    public UpdateHandler updateHandler(HandlerMethodPopular handlerMethodLookup) {
      return new MvcUpdateHandler(handlerMethodLookup);
    }
  }
}
