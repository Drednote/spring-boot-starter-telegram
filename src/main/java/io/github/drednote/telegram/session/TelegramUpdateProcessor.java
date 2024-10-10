package io.github.drednote.telegram.session;

import java.util.List;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramUpdateProcessor {

    void process(List<Update> update);
}
