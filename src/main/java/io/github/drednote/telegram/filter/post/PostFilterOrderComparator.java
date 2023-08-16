package io.github.drednote.telegram.filter.post;

import io.github.drednote.telegram.filter.FilterOrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class PostFilterOrderComparator extends FilterOrderComparator {

  public static final PostFilterOrderComparator INSTANCE = new PostFilterOrderComparator();

  private PostFilterOrderComparator() {
    super(PriorityPostUpdateFilter.class);
  }

  /**
   * Find an order value indicated by the given object.
   *
   * @param obj the object to check
   * @return the order value, or {@code null} if none found
   */
  @Override
  @Nullable
  protected Integer findOrder(@NonNull Object obj) {
    if (obj instanceof PostUpdateFilter filter) {
      return filter.getPostOrder();
    }
    return null;
  }
}
