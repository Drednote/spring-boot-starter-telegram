package com.github.drednote.telegram.session;


import org.telegram.telegrambots.meta.generics.TelegramBot;

public interface TelegramBotSession {

  void setCallback(TelegramBot callback);

  void start();

  /**
   * Stops the bot
   */
  void stop();
}
