package io.github.drednote.telegram.core.comparator;

import java.util.Comparator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Provides a base implementation for comparing objects of a specific type. Subclasses must
 * implement the {@link #doCompare} method to define the comparison logic
 *
 * @param <T> The type of objects being compared
 * @author Galushko Ivan
 */
public abstract class DefaultComparator<T> implements Comparator<T> {

  /**
   * Compares two objects of type {@code T} using the specific comparison logic defined in this
   * method. If either of the objects is null, returns a positive or negative value based on which
   * object is null
   *
   * @param o1 The first object to compare, not null
   * @param o2 The second object to compare, not null
   * @return A negative integer, zero, or a positive integer as the first object is less than, equal
   * to, or greater than the second object
   */
  protected abstract int doCompare(@NonNull T o1, @NonNull T o2);

  /**
   * Compares two objects of type {@code T} using the specific comparison logic defined in
   * {@link #doCompare}. If either of the objects is null, it returns a positive or negative value
   * based on which object is null
   *
   * @param o1 The first object to compare, nullable
   * @param o2 The second object to compare, nullable
   * @return A negative integer, zero, or a positive integer as the first object is less than, equal
   * to, or greater than the second object
   */
  public int compare(@Nullable T o1, @Nullable T o2) {
    if (o1 == null) {
      return 1;
    }
    if (o2 == null) {
      return -1;
    }
    return doCompare(o1, o2);
  }
}
