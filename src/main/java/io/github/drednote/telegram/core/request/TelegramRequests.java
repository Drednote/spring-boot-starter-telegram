package io.github.drednote.telegram.core.request;

import java.util.Set;

public class TelegramRequests {

    public static TelegramRequestImpl text(String... patterns) {
        return new TelegramRequestImpl(
                Set.of(patterns), Set.of(RequestType.MESSAGE), Set.of(MessageType.TEXT), false);
    }

    public static TelegramRequestImpl command(String... patterns) {
        return new TelegramRequestImpl(
            Set.of(patterns), Set.of(RequestType.MESSAGE), Set.of(MessageType.COMMAND), false);
    }

    public static TelegramRequestImpl callbackQuery(String... patterns) {
        return new TelegramRequestImpl(
            Set.of(patterns), Set.of(RequestType.CALLBACK_QUERY), Set.of(), false);
    }
}
