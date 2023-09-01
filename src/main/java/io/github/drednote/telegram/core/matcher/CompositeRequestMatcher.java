package io.github.drednote.telegram.core.matcher;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collection;
import java.util.List;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * The {@code CompositeRequestMatcher} class is an implementation of the {@code RequestMatcher}
 * interface that represents a composite matcher. It contains a list of individual matchers and
 * checks whether any of them matches the given update request
 *
 * @author Ivan Galushko
 */
public class CompositeRequestMatcher implements RequestMatcher {

  /**
   * The list of individual matchers
   */
  private final List<RequestMatcher> matchers;

  /**
   * Creates a new instance of the {@code CompositeRequestMatcher} class with the given collection
   * of matchers
   *
   * @param matchers the collection of individual matchers, not null
   */
  public CompositeRequestMatcher(Collection<RequestMatcher> matchers) {
    Assert.required(matchers, "RequestMatchers");

    this.matchers = matchers.stream()
        .filter(it -> it != this)
        .sorted(AnnotationAwareOrderComparator.INSTANCE)
        .toList();
  }

  /**
   * Checks whether any of the individual matchers matches the given update request
   *
   * @param request the update request to match, not null
   * @return true if any of the matchers matches the request, false otherwise
   */
  @Override
  public boolean matches(UpdateRequest request) {
    Assert.notNull(request, "UpdateRequest");

    return matchers.stream().anyMatch(requestMatcher -> requestMatcher.matches(request));
  }
}
