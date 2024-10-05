package io.github.drednote.telegram.filter.post;

import io.github.drednote.telegram.filter.FilterOrderComparator;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Comparator for ordering post-update filters based on their priority.
 *
 * <p>This class extends the {@link FilterOrderComparator} and is specifically designed
 * for comparing post-update filter objects and their order values.
 *
 * @author Ivan Galushko
 * @see PostUpdateFilter
 * @see ConclusivePostUpdateFilter
 * @see Ordered
 */
public class PostFilterOrderComparator extends FilterOrderComparator {

    public static final PostFilterOrderComparator INSTANCE = new PostFilterOrderComparator();

    /**
     * Constructs a FilterOrderComparator with the specified priority class
     */
    private PostFilterOrderComparator() {
        super(null);
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
        } else if (obj instanceof ConclusivePostUpdateFilter filter) {
            return filter.getPostOrder();
        }
        return null;
    }
}
