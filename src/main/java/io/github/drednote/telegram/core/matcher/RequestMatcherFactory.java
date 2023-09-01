package io.github.drednote.telegram.core.matcher;

import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The {@code RequestMatcherFactory} class is a factory class for creating request matchers based on
 * a {@link UpdateRequestMapping}. It provides static methods for creating request matchers and
 * retrieving a collection of matchers for a {@code UpdateRequestMapping}
 *
 * @author Ivan Galushko
 * @see RequestMatcher
 */
public abstract class RequestMatcherFactory {

  private RequestMatcherFactory() {
  }

  /**
   * Creates a request matcher based on the given {@code UpdateRequestMapping}
   *
   * @param mapping the Telegram request mapping
   * @return the request matcher
   */
  public static RequestMatcher create(UpdateRequestMapping mapping) {
    return new CompositeRequestMatcher(getMatchers(mapping));
  }

  /**
   * Retrieves a collection of request matchers for the given {@code UpdateRequestMapping}
   *
   * @param info the Telegram request mapping
   * @return the collection of request matchers
   */
  public static Collection<RequestMatcher> getMatchers(UpdateRequestMapping info) {
    Collection<RequestMatcher> matchers = new ArrayList<>();

    RequestAndMessageTypeMatcher base = new RequestAndMessageTypeMatcher(info);
    matchers.add(base.thenMatching(new TextRequestMatcher(info)));

    return matchers;
  }
}
