package io.github.drednote.telegram.filter;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class PreFilterOrderComparator extends FilterOrderComparator {

  public static final PreFilterOrderComparator INSTANCE = new PreFilterOrderComparator();

  private PreFilterOrderComparator() {
    super(PriorityPreUpdateFilter.class);
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
    if (obj instanceof PreUpdateFilter filter) {
      return filter.getPreOrder();
    }
    return null;
  }
}
