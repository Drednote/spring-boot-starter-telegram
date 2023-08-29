package io.github.drednote.telegram.filter.post;

/**
 * Marker interface to indicate a priority for post-update filters.
 *
 * <p>This interface extends the {@link PostUpdateFilter} interface and serves as a marker
 * to denote a priority for post-update filters. Classes implementing this interface can be
 * considered as post-update filters with a higher priority than {@code PostUpdateFilter}.
 *
 * @author Ivan Galushko
 * @see PostUpdateFilter
 * @see PostFilterOrderComparator
 */
public interface PriorityPostUpdateFilter extends PostUpdateFilter {

}
