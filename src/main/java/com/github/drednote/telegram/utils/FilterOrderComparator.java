package com.github.drednote.telegram.utils;

import com.github.drednote.telegram.filter.PriorityUpdateFilter;
import com.github.drednote.telegram.filter.UpdateFilter;
import org.springframework.core.OrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class FilterOrderComparator extends OrderComparator {

  public static final FilterOrderComparator PRE_INSTANCE = new FilterOrderComparator(true);
  public static final FilterOrderComparator POST_INSTANCE = new FilterOrderComparator(false);
  private final boolean pre;

  private FilterOrderComparator(boolean pre) {
    this.pre = pre;
  }

  @Override
  public int compare(@Nullable Object o1, @Nullable Object o2) {
    return doCompare(o1, o2);
  }

  private int doCompare(@Nullable Object o1, @Nullable Object o2) {
    boolean p1 = (o1 instanceof PriorityUpdateFilter);
    boolean p2 = (o2 instanceof PriorityUpdateFilter);
    if (p1 && !p2) {
      return -1;
    } else if (p2 && !p1) {
      return 1;
    }

    int i1 = getOrder(o1);
    int i2 = getOrder(o2);
    return Integer.compare(i1, i2);
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
    if (obj instanceof UpdateFilter filter) {
      return pre ? filter.getPreOrder() : filter.getPostOrder();
    }
    return null;
  }
}
