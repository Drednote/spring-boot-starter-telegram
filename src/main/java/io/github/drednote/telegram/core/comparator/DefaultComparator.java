package io.github.drednote.telegram.core.comparator;

import java.util.Comparator;

public abstract class DefaultComparator<T> implements Comparator<T> {

  protected abstract int doCompare(T o1, T o2);

  public int compare(T o1, T o2) {
    if (o1 == null) {
      return 1;
    }
    if (o2 == null) {
      return -1;
    }
    return doCompare(o1, o2);
  }
}
