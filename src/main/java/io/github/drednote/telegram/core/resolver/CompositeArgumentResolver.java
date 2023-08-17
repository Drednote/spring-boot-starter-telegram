package io.github.drednote.telegram.core.resolver;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collection;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;

public class CompositeArgumentResolver implements HandlerMethodArgumentResolver {

  private final List<HandlerMethodArgumentResolver> resolvers;

  public CompositeArgumentResolver(
      @NonNull Collection<HandlerMethodArgumentResolver> resolvers
  ) {
    Assert.notNull(resolvers, "resolvers");

    resolvers.add(new TelegramPatternVariableArgumentResolver());
    resolvers.add(new UpdateAccessorsArgumentResolver());
    resolvers.add(new RequestArgumentResolver());

    this.resolvers = resolvers.stream()
        .filter(it -> it.getClass() != this.getClass())
        .sorted(AnnotationAwareOrderComparator.INSTANCE)
        .toList();
  }

  @Override
  public Object resolveArgument(@NonNull MethodParameter parameter, @NonNull TelegramUpdateRequest request) {
    for (HandlerMethodArgumentResolver resolver : resolvers) {
      if (resolver.supportsParameter(parameter)) {
        return resolver.resolveArgument(parameter, request);
      }
    }
    throw new IllegalStateException("No suitable resolver");
  }

  @Override
  public boolean supportsParameter(@NonNull MethodParameter parameter) {
    return resolvers.stream().anyMatch(resolver -> resolver.supportsParameter(parameter));
  }

  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE;
  }
}
