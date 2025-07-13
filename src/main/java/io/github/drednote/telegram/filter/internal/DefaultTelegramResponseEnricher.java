package io.github.drednote.telegram.filter.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.response.AbstractTelegramResponse;
import io.github.drednote.telegram.response.SimpleMessageTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.response.TelegramResponseHelper;
import io.github.drednote.telegram.response.resolver.CompositeTelegramResponseTypesResolver;
import io.github.drednote.telegram.response.resolver.TelegramResponseTypesResolver;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collection;

/**
 * Default implementation of {@link TelegramResponseEnricher} that enriches {@link TelegramResponse} instances with
 * values derived from application configuration and dependencies.
 * <p>
 * This implementation handles multiple response types and injects:
 * <ul>
 *   <li>{@link TelegramMessageSource} — for localizable messages (used in {@link SimpleMessageTelegramResponse})</li>
 *   <li>{@code parseMode} — from {@link TelegramProperties}</li>
 *   <li>{@link ObjectMapper} — for object serialization in responses</li>
 *   <li>{@link TelegramResponseTypesResolver} — to resolve response types</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is typically used before dispatching a response back to Telegram,
 * ensuring that all dynamic or required fields are filled automatically.
 *
 * @author Ivan Galushko
 * @implNote If you modify the enrichment logic, make sure to reflect changes in {@link TelegramResponseHelper}.
 */
public class DefaultTelegramResponseEnricher implements TelegramResponseEnricher {

    /**
     * The object mapper for serializing and deserializing JSON
     */
    private final ObjectMapper objectMapper;
    /**
     * The Telegram properties
     */
    private final TelegramProperties telegramProperties;
    /**
     * The message source for retrieving localized messages
     */
    private final TelegramMessageSource messageSource;
    private final TelegramResponseTypesResolver resolver;

    /**
     * Constructs a new instance of {@link DefaultTelegramResponseEnricher}.
     *
     * @param objectMapper       the Jackson {@link ObjectMapper} for serializing objects
     * @param telegramProperties configuration properties for Telegram
     * @param messageSource      source for localized message resolution
     * @param resolvers          collection of {@link TelegramResponseTypesResolver} implementations, maybe empty
     */
    public DefaultTelegramResponseEnricher(
        ObjectMapper objectMapper, TelegramProperties telegramProperties,
        TelegramMessageSource messageSource, Collection<TelegramResponseTypesResolver> resolvers
    ) {
        Assert.required(objectMapper, "ObjectMapper");
        Assert.required(telegramProperties, "TelegramProperties");
        Assert.required(messageSource, "TelegramMessageSource");
        Assert.required(resolvers, "Collection of TelegramResponseTypesResolver");

        this.objectMapper = objectMapper;
        this.telegramProperties = telegramProperties;
        this.messageSource = messageSource;
        this.resolver = new CompositeTelegramResponseTypesResolver(resolvers);
    }

    /**
     * Enriches a {@link TelegramResponse} instance by populating required dependencies and default values.
     * <p>
     * Handles both {@link SimpleMessageTelegramResponse} and {@link AbstractTelegramResponse} hierarchies.
     *
     * @param telegramResponse the response to be enriched
     */
    @Override
    public void enrich(TelegramResponse telegramResponse) {
        if (telegramResponse instanceof SimpleMessageTelegramResponse simpleMessageTelegramResponse) {
            if (simpleMessageTelegramResponse.getMessageSource() == null) {
                simpleMessageTelegramResponse.setMessageSource(messageSource);
            }
        }
        if (telegramResponse instanceof AbstractTelegramResponse abstractTelegramResponse) {
            if (abstractTelegramResponse.getParseMode() == null) {
                abstractTelegramResponse.setParseMode(telegramProperties.getUpdateHandler().getParseMode());
            }
            if (abstractTelegramResponse.getResolver() == null) {
                abstractTelegramResponse.setResolver(resolver);
            }
            if (abstractTelegramResponse.getSerializeJavaObjectWithJackson() == null) {
                abstractTelegramResponse.setSerializeJavaObjectWithJackson(
                    telegramProperties.getUpdateHandler().isSerializeJavaObjectWithJackson());
            }
            if (abstractTelegramResponse.getObjectMapper() == null) {
                abstractTelegramResponse.setObjectMapper(objectMapper);
            }
        }
    }
}
