package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.internal.TelegramResponseEnricher;
import io.github.drednote.telegram.utils.Assert;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

public class TelegramResponseHelper {

    private final TelegramResponse telegramResponse;

    public TelegramResponseHelper(TelegramResponse telegramResponse) {
        Assert.required(telegramResponse, "TelegramResponse");
        this.telegramResponse = telegramResponse;
    }

    public static TelegramResponseHelper create(TelegramResponse telegramResponse) {
        return new TelegramResponseHelper(telegramResponse);
    }

    /**
     * @param from the response where take the properties.
     * @return this instance
     * @implNote When change the algorithm, remember to change {@link TelegramResponseEnricher}.
     */
    public TelegramResponseHelper propagateProperties(AbstractTelegramResponse from) {
        if (telegramResponse instanceof AbstractTelegramResponse abstractTelegramResponse) {
            if (from.getParseMode() != null && abstractTelegramResponse.getParseMode() == null) {
                abstractTelegramResponse.setParseMode(from.getParseMode());
            }
            if (from.getResolver() != null && abstractTelegramResponse.getResolver() == null) {
                abstractTelegramResponse.setResolver(from.getResolver());
            }
            if (from.getSerializeJavaObjectWithJackson() != null
                && abstractTelegramResponse.getSerializeJavaObjectWithJackson() == null) {
                abstractTelegramResponse.setSerializeJavaObjectWithJackson(
                    from.getSerializeJavaObjectWithJackson());
            }
            if (from.getObjectMapper() != null && abstractTelegramResponse.getObjectMapper() == null) {
                abstractTelegramResponse.setObjectMapper(from.getObjectMapper());
            }
        }
        return this;
    }

    public void process(UpdateRequest request) throws TelegramApiException {
        telegramResponse.process(request);
    }

    public Mono<Void> processReactive(UpdateRequest request) {
        return telegramResponse.processReactive(request);
    }
}
