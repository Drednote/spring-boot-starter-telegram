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
     * @param objectMapper       the object mapper, not null
     * @param telegramProperties the Telegram properties, not null
     * @param messageSource      The TelegramMessageSource instance for retrieving localized messages
     * @param resolvers          resolvers
     */
    public DefaultTelegramResponseEnricher(
        ObjectMapper objectMapper, TelegramProperties telegramProperties,
        TelegramMessageSource messageSource, Collection<TelegramResponseTypesResolver> resolvers
    ) {
        Assert.required(objectMapper, "ObjectMapper");
        Assert.required(messageSource, "TelegramMessageSource");

        this.objectMapper = objectMapper;
        this.telegramProperties = telegramProperties;
        this.messageSource = messageSource;
        this.resolver = new CompositeTelegramResponseTypesResolver(resolvers);
    }

    /**
     * @param telegramResponse response to Telegram
     * @implNote When change the algorithm, remember to change {@link TelegramResponseHelper}.
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
