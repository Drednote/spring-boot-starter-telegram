package io.github.drednote.telegram.core.request;

import io.github.drednote.telegram.core.comparator.RequestMappingInfoComparator;
import io.github.drednote.telegram.core.matcher.RequestMatcher;
import io.github.drednote.telegram.core.matcher.RequestMatcherFactory;
import io.github.drednote.telegram.utils.Assert;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * The {@code UpdateRequestMapping} class represents a mapping for handling Telegram update
 * requests. It implements the {@link Comparable} interface to allow for sorting and the
 * {@link RequestMatcher} interface to determine if a request matches the mapping
 *
 * @author Ivan Galushko
 */
public class UpdateRequestMapping implements Comparable<UpdateRequestMapping>, RequestMatcher {

  /**
   * The pattern associated with the mapping
   */
  @Getter
  private final String pattern;
  /**
   * The type of the request associated with the mapping
   */
  @Getter
  @Nullable
  private final RequestType requestType;
  /**
   * The types of messages associated with the mapping
   */
  @Getter
  private final Set<MessageType> messageTypes;

  /**
   * The path matcher used for matching patterns
   */
  @Getter
  private final PathMatcher pathMatcher = new AntPathMatcher();
  /**
   * The request matcher used for matching update requests
   */
  private final RequestMatcher requestMatcher = RequestMatcherFactory.create(this);
  /**
   * The comparator used for comparing request mappings
   */
  private final RequestMappingInfoComparator comparator =
      new RequestMappingInfoComparator(pathMatcher);

  public UpdateRequestMapping(
      @NonNull String pattern, @Nullable RequestType requestType,
      @NonNull Set<MessageType> messageTypes
  ) {
    Assert.required(pattern, "Pattern");
    Assert.notNull(messageTypes, "MessageTypes");

    this.pattern = pattern;
    this.requestType = requestType;
    this.messageTypes = messageTypes;
  }

  /**
   * Compares this mapping to another mapping for sorting purposes
   *
   * @param o the mapping to compare to, not null
   * @return the comparison result
   */
  @Override
  public int compareTo(@NonNull UpdateRequestMapping o) {
    return comparator.compare(this, o);
  }

  /**
   * Determines if the given update request matches this mapping
   *
   * @param request the update request to match, not null
   * @return true if the request matches, false otherwise
   */
  @Override
  public boolean matches(@NonNull UpdateRequest request) {
    return requestMatcher.matches(request);
  }

  @Override
  public String toString() {
    return "{%s#%s %s}".formatted(
        requestType != null ? requestType : "ALL",
        !messageTypes.isEmpty() ? messageTypes : "ALL",
        pattern
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateRequestMapping that = (UpdateRequestMapping) o;
    return Objects.equals(pattern, that.pattern) && requestType == that.requestType
        && messageTypes.equals(that.messageTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pattern, requestType, messageTypes);
  }
}
