package io.github.drednote.telegram.filter;

import org.springframework.core.OrderComparator;
import org.springframework.lang.Nullable;

/**
 * Abstract comparator for ordering filter objects based on their priority.
 *
 * <p>This class extends the {@link OrderComparator} and provides common logic for comparing
 * filter objects based on their priority classes and order values.
 *
 * <p>Classes extending this abstract comparator should provide an implementation for the
 * {@link #findOrder(Object)} method to retrieve the order value from the filter object.
 *
 * @author Ivan Galushko
 * @see OrderComparator
 */
public abstract class FilterOrderComparator extends OrderComparator {

    @Nullable
    private final Class<?> priorityClazz;

    /**
     * Constructs a FilterOrderComparator with the specified priority class
     *
     * @param priorityClazz The priority class for comparison, not null
     */
    protected FilterOrderComparator(@Nullable Class<?> priorityClazz) {
        this.priorityClazz = priorityClazz;
    }

    /**
     * Compares two objects based on their priority and order values
     *
     * @param o1 The first object to compare, nullable
     * @param o2 The second object to compare, nullable
     * @return A negative integer, zero, or a positive integer as the first argument is less than,
     * equal to, or greater than the second
     */
    @Override
    public int compare(@Nullable Object o1, @Nullable Object o2) {
        return doCompare(o1, o2);
    }

    private int doCompare(@Nullable Object o1, @Nullable Object o2) {
        if (priorityClazz != null) {
            boolean p1 = o1 != null && priorityClazz.isAssignableFrom(o1.getClass());
            boolean p2 = o2 != null && priorityClazz.isAssignableFrom(o2.getClass());
            if (p1 && !p2) {
                return -1;
            } else if (p2 && !p1) {
                return 1;
            }
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
    protected abstract Integer findOrder(Object obj);
}
