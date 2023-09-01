package io.github.drednote.telegram.core.matcher;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.util.Objects;
import org.springframework.core.Ordered;
import org.springframework.util.PathMatcher;

/**
 * The TextRequestMatcher class is an implementation of the RequestMatcher interface that matches
 * requests based on the text of the request. It checks if the text of the given update request
 * matches the text specified in the mapping.
 *
 * @author Ivan Galushko
 */
public class TextRequestMatcher implements RequestMatcher {

  /**
   * The Telegram request mapping
   */
  private final UpdateRequestMapping mapping;

  /**
   * Creates a new instance of the {@code TextRequestMatcher} class with the given mapping
   *
   * @param mapping the Telegram request mapping, not null
   */
  public TextRequestMatcher(UpdateRequestMapping mapping) {
    Assert.required(mapping, "UpdateRequestMapping");

    this.mapping = mapping;
  }

  /**
   * Checks if the text of the given update request matches the text specified in the mapping.
   * Delegate matching to {@link PathMatcher}
   *
   * @param request the update request to match, not null
   * @return true if the text matches, false otherwise
   */
  @Override
  public boolean matches(UpdateRequest request) {
    Assert.notNull(request, "UpdateRequest");

    String text = defaultIfNull(request.getText(), "");
    String pattern = mapping.getPattern();
    if (Objects.equals(text, pattern)) {
      return true;
    }
    PathMatcher pathMatcher = mapping.getPathMatcher();
    return pathMatcher.match(pattern, text);
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 1;
  }
}
