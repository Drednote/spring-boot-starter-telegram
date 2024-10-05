package io.github.drednote.telegram.core;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramBot {

    void onUpdateReceived(Update update);
}
