package io.github.drednote.telegram.filter;

import org.springframework.core.Ordered;

/**
 * Constants with order of filters.
 */
public final class FilterOrder {

    public static final Integer HIGHEST_PRECEDENCE = Ordered.HIGHEST_PRECEDENCE + 100;
    public static final Integer DEFAULT_PRECEDENCE = Ordered.HIGHEST_PRECEDENCE + 200;

    private FilterOrder() {
    }
}
