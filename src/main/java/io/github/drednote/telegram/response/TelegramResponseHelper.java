package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramResponseHelper {

    private final TelegramResponse telegramResponse;

    public TelegramResponseHelper(TelegramResponse telegramResponse) {
        Assert.required(telegramResponse, "TelegramResponse");
        this.telegramResponse = telegramResponse;
    }

    public static TelegramResponseHelper create(TelegramResponse telegramResponse) {
        return new TelegramResponseHelper(telegramResponse);
    }

    public TelegramResponseHelper propagateProperties(AbstractTelegramResponse from) {
        if (telegramResponse instanceof AbstractTelegramResponse abstractTelegramResponse) {
            if (from.getParseMode() != null) {
                abstractTelegramResponse.setParseMode(from.getParseMode());
            }
            if (from.getResolver() != null) {
                abstractTelegramResponse.setResolver(from.getResolver());
            }
        }
        return this;
    }

    public void process(UpdateRequest request) throws TelegramApiException {
        telegramResponse.process(request);
    }
}
