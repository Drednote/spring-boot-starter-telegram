package io.github.drednote.telegram.filter;

import org.springframework.core.OrderComparator;
import org.springframework.lang.Nullable;

public class FilterOrderComparator extends OrderComparator {

  private final Class<?> priorityClazz;

  protected FilterOrderComparator(Class<?> priorityClazz) {
    this.priorityClazz = priorityClazz;
  }

  @Override
  public int compare(@Nullable Object o1, @Nullable Object o2) {
    return doCompare(o1, o2);
  }

  private int doCompare(@Nullable Object o1, @Nullable Object o2) {
    boolean p1 = o1 != null && priorityClazz.isAssignableFrom(o1.getClass());
    boolean p2 = o2 != null && priorityClazz.isAssignableFrom(o2.getClass());
    if (p1 && !p2) {
      return -1;
    } else if (p2 && !p1) {
      return 1;
    }

    int i1 = getOrder(o1);
    int i2 = getOrder(o2);
    return Integer.compare(i1, i2);
  }
}
