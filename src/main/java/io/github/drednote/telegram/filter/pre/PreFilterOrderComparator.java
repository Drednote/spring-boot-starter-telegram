package io.github.drednote.telegram.filter.pre;

import io.github.drednote.telegram.filter.FilterOrderComparator;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Comparator for ordering pre-update filters based on their priority.
 *
 * <p>This class extends the {@link FilterOrderComparator} and is specifically designed
 * for comparing pre-update filter objects and their order values.
 *
 * @author Ivan Galushko
 * @see PreUpdateFilter
 * @see Ordered
 */
public class PreFilterOrderComparator extends FilterOrderComparator {

  public static final PreFilterOrderComparator INSTANCE = new PreFilterOrderComparator();

  /**
   * Constructs a FilterOrderComparator with the specified priority class
   */
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
