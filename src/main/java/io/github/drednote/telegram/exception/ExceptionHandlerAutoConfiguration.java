package io.github.drednote.telegram.exception;

import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ExceptionHandlerAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public ExceptionHandler exceptionHandler(
      ExceptionHandlerResolver exceptionHandlerResolver, HandlerMethodInvoker handlerMethodInvoker
  ) {
    return new DefaultExceptionHandler(exceptionHandlerResolver, handlerMethodInvoker);
  }

  @Bean
  @ConditionalOnMissingBean
  public DefaultExceptionHandlerResolver exceptionHandlerResolver() {
    return new DefaultExceptionHandlerResolver();
  }

  @Bean
  public TelegramAdviceBeanPostProcessor telegramAdviceBeanPostProcessor(
      ExceptionHandlerRegistrar registrar
  ) {
    return new TelegramAdviceBeanPostProcessor(registrar);
  }
}
