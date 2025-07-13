package io.github.drednote.telegram.response.resolver;

import io.github.drednote.telegram.response.TelegramResponse;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/**
 * Strategy interface for resolving a given response object to a {@link TelegramResponse}.
 * <p>
 * Implementations can define how different response types (e.g., {@code String}, DTOs, domain objects) are mapped or
 * transformed into a specific {@link TelegramResponse} instance.
 *
 * <p>
 * Supports ordering via {@link Ordered}, allowing multiple resolvers to be combined with precedence control.
 *
 * @author Ivan Galushko
 */
public interface TelegramResponseTypesResolver extends Ordered {

    /**
     * Attempts to convert the provided object into a {@link TelegramResponse}.
     *
     * @param response the input object to convert
     * @return a {@link TelegramResponse} instance or {@code null} if this resolver does not support the type
     */
    @Nullable
    TelegramResponse resolve(Object response);

    /**
     * Defines the order in which this resolver is evaluated when part of a composite resolver chain.
     *
     * @return the order value (lower means higher priority)
     */
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
