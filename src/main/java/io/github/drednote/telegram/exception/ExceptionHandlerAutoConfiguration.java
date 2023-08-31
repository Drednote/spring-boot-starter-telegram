package io.github.drednote.telegram.exception;

import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * The {@code ExceptionHandlerAutoConfiguration} class is an autoconfiguration class responsible for
 * defining and providing beans related to exception handling
 *
 * @author Ivan Galushko
 */
@AutoConfiguration
public class ExceptionHandlerAutoConfiguration {

  /**
   * Creates and configures a bean for the {@code ExceptionHandler}, using the provided {@code
   * ExceptionHandlerResolver} and {@code HandlerMethodInvoker}
   *
   * @param exceptionHandlerResolver The resolver for mapping exceptions to handler methods
   * @param handlerMethodInvoker     The invoker for handling method invocation
   * @return The configured {@code ExceptionHandler} bean
   */
  @Bean
  @ConditionalOnMissingBean
  public ExceptionHandler exceptionHandler(
      ExceptionHandlerResolver exceptionHandlerResolver, HandlerMethodInvoker handlerMethodInvoker
  ) {
    return new DefaultExceptionHandler(exceptionHandlerResolver, handlerMethodInvoker);
  }

  /**
   * Creates and configures a bean for the {@code DefaultExceptionHandlerResolver}
   *
   * @return The configured {@code DefaultExceptionHandlerResolver} bean
   */
  @Bean
  @ConditionalOnMissingBean
  public DefaultExceptionHandlerResolver exceptionHandlerResolver() {
    return new DefaultExceptionHandlerResolver();
  }

  /**
   * Creates and configures a bean for the {@link TelegramAdviceBeanPostProcessor}
   *
   * @param registrar The registrar for adding exception handling advice
   * @return The configured {@link TelegramAdviceBeanPostProcessor} bean
   */
  @Bean
  public TelegramAdviceBeanPostProcessor telegramAdviceBeanPostProcessor(
      ExceptionHandlerRegistrar registrar
  ) {
    return new TelegramAdviceBeanPostProcessor(registrar);
  }
}
