package io.github.drednote.telegram.core.request;

import java.util.Set;

public class TelegramRequests {

    public static DefaultTelegramRequest text(String... patterns) {
        return new DefaultTelegramRequest(Set.of(patterns), Set.of(RequestType.MESSAGE), Set.of(MessageType.TEXT));
    }

    public static DefaultTelegramRequest command(String... patterns) {
        return new DefaultTelegramRequest(Set.of(patterns), Set.of(RequestType.MESSAGE), Set.of(MessageType.COMMAND));
    }

    public static DefaultTelegramRequest callbackQuery(String... patterns) {
        return new DefaultTelegramRequest(Set.of(patterns), Set.of(RequestType.CALLBACK_QUERY));
    }
}
