package io.github.drednote.telegram.filter.pre;

/**
 * Marker interface to indicate a priority for pre-update filters.
 *
 * <p>This interface extends the {@link PreUpdateFilter} interface and serves as a marker
 * to denote a priority for pre-update filters. Classes implementing this interface can be
 * considered as pre-update filters with a higher priority than {@code PreUpdateFilter}.
 *
 * @author Ivan Galushko
 * @see PreUpdateFilter
 * @see PreFilterOrderComparator
 */
public interface PriorityPreUpdateFilter extends PreUpdateFilter {

}
