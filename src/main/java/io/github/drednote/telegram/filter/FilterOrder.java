package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.filter.post.ConclusivePostUpdateFilter;
import io.github.drednote.telegram.filter.post.ScenarioIdUpdateFilter;
import io.github.drednote.telegram.filter.post.SendResponseFilter;
import io.github.drednote.telegram.filter.pre.AccessPermissionFilter;
import io.github.drednote.telegram.filter.pre.ControllerUpdateHandlerPopular;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import io.github.drednote.telegram.filter.pre.PriorityPreUpdateFilter;
import io.github.drednote.telegram.filter.pre.RoleFilter;
import io.github.drednote.telegram.filter.pre.ScenarioUpdateHandlerPopular;
import io.github.drednote.telegram.filter.pre.UserRateLimitRequestFilter;
import java.util.Map;
import org.springframework.core.Ordered;

/**
 * Constants with order of filters.
 */
public final class FilterOrder {

    // ------- Default orders ------- //
    public static final int HIGHEST_PRECEDENCE = Ordered.HIGHEST_PRECEDENCE + 100;
    public static final int DEFAULT_PRECEDENCE = Ordered.HIGHEST_PRECEDENCE + 200;
    public static final int LOWEST_PRECEDENCE = Ordered.LOWEST_PRECEDENCE - 100;

    // ------- Orders for priority pre filters ------- //
    public static final Map<Class<? extends PriorityPreUpdateFilter>, Integer> PRIORITY_PRE_FILTERS = Map.of(
        UserRateLimitRequestFilter.class, FilterOrder.HIGHEST_PRECEDENCE,
        RoleFilter.class, FilterOrder.HIGHEST_PRECEDENCE + 100,
        ScenarioUpdateHandlerPopular.class, FilterOrder.HIGHEST_PRECEDENCE + 200,
        ControllerUpdateHandlerPopular.class, FilterOrder.HIGHEST_PRECEDENCE + 300
    );

    // ------- Orders for pre filters ------- //
    public static final Map<Class<? extends PreUpdateFilter>, Integer> PRE_FILTERS = Map.of(
        AccessPermissionFilter.class, FilterOrder.HIGHEST_PRECEDENCE
    );


    // ------- Orders for post filters ------- //
    public static final int NOT_HANDLED_FILTER_PRECEDENCE = FilterOrder.LOWEST_PRECEDENCE;

    // ------- Orders for conclusive post filters ------- //
    public static final Map<Class<? extends ConclusivePostUpdateFilter>, Integer> CONCLUSIVE_POST_FILTERS = Map.of(
        SendResponseFilter.class, FilterOrder.HIGHEST_PRECEDENCE,
        ScenarioIdUpdateFilter.class, FilterOrder.HIGHEST_PRECEDENCE + 100
    );

    private FilterOrder() {
    }
}
