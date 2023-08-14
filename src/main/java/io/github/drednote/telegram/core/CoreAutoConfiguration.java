package io.github.drednote.telegram.core;

import io.github.drednote.telegram.core.invoke.DefaultHandlerMethodInvoker;
import io.github.drednote.telegram.core.invoke.HandlerMethodInvoker;
import io.github.drednote.telegram.core.resolver.CompositeArgumentResolver;
import io.github.drednote.telegram.core.resolver.HandlerMethodArgumentResolver;
import java.util.Collection;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CoreAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public HandlerMethodInvoker handlerMethodInvoker(
      Collection<HandlerMethodArgumentResolver> resolvers
  ) {
    return new DefaultHandlerMethodInvoker(new CompositeArgumentResolver(resolvers));
  }
}
