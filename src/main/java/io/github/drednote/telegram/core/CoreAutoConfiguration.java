package io.github.drednote.telegram.core;

import io.github.drednote.telegram.core.invoke.DefaultHandlerMethodInvoker;
import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.core.resolver.CompositeArgumentResolver;
import io.github.drednote.telegram.core.resolver.HandlerMethodArgumentResolver;
import java.util.Collection;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * The {@code CoreAutoConfiguration} class provides configuration for the beans of core package
 *
 * @author Ivan Galushko
 */
@AutoConfiguration
public class CoreAutoConfiguration {

  /**
   * Creates a {@link HandlerMethodInvoker} bean if no other bean of the same type is present
   *
   * @param resolvers the collection of argument resolvers
   * @return the {@code HandlerMethodInvoker} bean
   */
  @Bean
  @ConditionalOnMissingBean
  public HandlerMethodInvoker handlerMethodInvoker(
      Collection<HandlerMethodArgumentResolver> resolvers
  ) {
    return new DefaultHandlerMethodInvoker(new CompositeArgumentResolver(resolvers));
  }
}
