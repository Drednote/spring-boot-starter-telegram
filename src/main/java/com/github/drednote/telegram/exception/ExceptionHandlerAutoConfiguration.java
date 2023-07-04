package com.github.drednote.telegram.exception;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ExceptionHandlerAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public ExceptionHandler exceptionHandler(ExceptionHandlerResolver exceptionHandlerResolver) {
    return new DefaultExceptionHandler(exceptionHandlerResolver);
  }

  @Bean
  public DefaultExceptionHandlerResolver exceptionHandlerResolver() {
    return new DefaultExceptionHandlerResolver();
  }

  @Bean
  public BotAdviceBeanPostProcessor botAdviceBeanPostProcessor(
      ExceptionHandlerRegistrar registrar
  ) {
    return new BotAdviceBeanPostProcessor(registrar);
  }
}
