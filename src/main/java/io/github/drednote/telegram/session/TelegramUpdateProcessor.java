package io.github.drednote.telegram.session;

import java.util.List;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Interface for processing updates received from Telegram.
 * <p>
 * Implementations of this interface are responsible for handling the list of Telegram updates and performing the
 * appropriate processing logic.
 *
 * @author Ivan Galushko
 */
public interface TelegramUpdateProcessor {

    /**
     * Processes a list of updates received from Telegram.
     *
     * @param update the list of updates from Telegram to process
     */
    void process(List<Update> update);
}
