package io.github.drednote.telegram.session.processor;

/**
 * Interface for reading Telegram updates.
 * <p>
 * Provides lifecycle methods to start and stop the message reading process, and a method for triggering an immediate
 * read, allowing implementations to manage how and when updates are fetched.
 *
 * @author Ivan Galushko
 */
public interface TelegramUpdateReader {

    /**
     * Starts the message reading process.
     */
    void start();

    /**
     * Stops the message reading process.
     */
    void stop();

    /**
     * Triggers an immediate read of messages.
     */
    void readImmediately();
}
