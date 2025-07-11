package io.github.drednote.telegram.response.resolver;

import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collection;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;

public class CompositeTelegramResponseTypesResolver implements TelegramResponseTypesResolver {

    private final Collection<TelegramResponseTypesResolver> resolvers;

    public CompositeTelegramResponseTypesResolver(Collection<TelegramResponseTypesResolver> resolvers) {
        Assert.required(resolvers, "Collection of TelegramResponseTypesResolver");

        this.resolvers = resolvers.stream()
            .filter(it -> it != this)
            .sorted(AnnotationAwareOrderComparator.INSTANCE)
            .toList();
    }

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
