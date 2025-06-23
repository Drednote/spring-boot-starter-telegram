package io.github.drednote.telegram.session;

import java.util.List;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramUpdateProcessor {

    /**
     * Processing update from the telegram.
     *
     * @param update update from telegram
     */
    void process(List<Update> update);
}
