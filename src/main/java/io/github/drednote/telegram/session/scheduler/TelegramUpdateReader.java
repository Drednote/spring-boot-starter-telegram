package io.github.drednote.telegram.session.scheduler;

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
     * Method that
     */
    void readImmediately();
}
