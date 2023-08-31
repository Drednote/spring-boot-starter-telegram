package io.github.drednote.telegram.core.resolver;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collection;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;

/**
 * The {@code CompositeArgumentResolver} class is an implementation of the
 * {@code HandlerMethodArgumentResolver} interface that combines multiple argument resolvers into a
 * composite resolver. It iterates through the list of resolvers and delegates the resolution of
 * method arguments to the first resolver that supports the parameter type
 *
 * @author Ivan Galushko
 */
public class CompositeArgumentResolver implements HandlerMethodArgumentResolver {

  /**
   * The list of resolvers in the composite resolver
   */
  private final List<HandlerMethodArgumentResolver> resolvers;

  /**
   * Creates a new instance of the {@code CompositeArgumentResolver} class with the specified
   * resolvers.
   *
   * @param resolvers the collection of resolvers to be included in the composite resolver, not
   *                  null
   */
  public CompositeArgumentResolver(Collection<HandlerMethodArgumentResolver> resolvers) {
    Assert.required(resolvers, "resolvers");

    resolvers.add(new TelegramPatternVariableArgumentResolver());
    resolvers.add(new UpdateAccessorsArgumentResolver());
    resolvers.add(new RequestArgumentResolver());

    this.resolvers = resolvers.stream()
        .filter(it -> it != this)
        .sorted(AnnotationAwareOrderComparator.INSTANCE)
        .toList();
  }

  /**
   * Delegating to the first resolver that supports the parameter type to resolve the argument value
   * for the given method parameter
   *
   * @param parameter the method parameter to resolve, not null
   * @param request   the update request, not null
   * @return the resolved argument value. Nullable
   * @throws IllegalStateException if no suitable resolver is found
   */
  @Override
  @Nullable
  public Object resolveArgument(MethodParameter parameter, TelegramUpdateRequest request) {
    Assert.notNull(parameter, "MethodParameter");
    Assert.notNull(request, "TelegramUpdateRequest");

    for (HandlerMethodArgumentResolver resolver : resolvers) {
      if (resolver.supportsParameter(parameter)) {
        return resolver.resolveArgument(parameter, request);
      }
    }
    throw new IllegalStateException("Not found suitable resolver");
  }

  /**
   * Checks if any of the resolvers in the composite resolver supports the given method parameter
   *
   * @param parameter the method parameter to check, not null
   * @return true if any of the resolvers supports the parameter, false otherwise
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    Assert.notNull(parameter, "MethodParameter");
    return resolvers.stream().anyMatch(resolver -> resolver.supportsParameter(parameter));
  }

  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE;
  }
}
