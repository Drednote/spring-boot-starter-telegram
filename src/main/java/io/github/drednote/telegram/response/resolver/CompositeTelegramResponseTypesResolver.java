package io.github.drednote.telegram.response.resolver;

import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collection;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;

/**
 * Composite implementation of {@link TelegramResponseTypesResolver} that delegates resolution to a list of nested
 * resolvers in priority order.
 * <p>
 * Resolvers are sorted using {@link AnnotationAwareOrderComparator}, allowing deterministic behavior when multiple
 * implementations are registered in the context.
 *
 * <p>
 * This class enables modular and extensible response mapping by chaining together multiple
 * {@link TelegramResponseTypesResolver} strategies.
 *
 * @author Ivan Galushko
 */
public class CompositeTelegramResponseTypesResolver implements TelegramResponseTypesResolver {

    private final Collection<TelegramResponseTypesResolver> resolvers;

    /**
     * Constructs a composite resolver from a collection of individual {@link TelegramResponseTypesResolver} instances.
     * <p>
     * The resolvers are sorted by order, and self-reference is excluded to prevent infinite recursion.
     *
     * @param resolvers a non-null collection of {@link TelegramResponseTypesResolver} instances
     */
    public CompositeTelegramResponseTypesResolver(Collection<TelegramResponseTypesResolver> resolvers) {
        Assert.required(resolvers, "Collection of TelegramResponseTypesResolver");

        this.resolvers = resolvers.stream()
            .filter(it -> it != this)
            .sorted(AnnotationAwareOrderComparator.INSTANCE)
            .toList();
    }

    /**
     * Delegates the resolution to the first resolver that successfully returns a non-null {@link TelegramResponse}.
     *
     * @param response the response object to resolve
     * @return a resolved {@link TelegramResponse} or {@code null} if none matched
     */
    @Override
    @Nullable
    public TelegramResponse resolve(Object response) {
        for (TelegramResponseTypesResolver resolver : resolvers) {
            TelegramResponse resolved = resolver.resolve(response);
            if (resolved != null) {
                return resolved;
            }
        }
        return null;
    }
}
