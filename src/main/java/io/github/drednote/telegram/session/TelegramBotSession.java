package io.github.drednote.telegram.session;

/**
 * Interface representing a session for managing a Telegram bot.
 *
 * <p>This interface defines methods for starting and stopping a session with a Telegram bot.
 * Implementing classes are responsible for managing the bot's lifecycle and interactions with the
 * Telegram server.
 *
 * @author Ivan Galushko
 * @see LongPollingSession
 */
public interface TelegramBotSession {

  /**
   * Starts the bot's session.
   *
   * <p>This method is responsible for initiating the bot's operation. It may involve setting
   * up necessary resources, establishing connections to the Telegram server, and scheduling tasks
   * to handle incoming updates.
   */
  void start();

  /**
   * Stops the bot's session.
   *
   * <p>This method is used to gracefully stop the bot's session. It should release any resources
   * acquired during the session, close connections, and perform necessary cleanup operations.
   */
  void stop();
}
