package com.github.drednote.telegrambot.bot;

import com.github.drednote.telegrambot.TelegramBotProperties;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class LongPollingBotContainer extends TelegramLongPollingBot {

  private final String name;

  public LongPollingBotContainer(TelegramBotProperties properties) {
    super(properties.getSession().toBotOptions(), properties.getToken());
    this.name = properties.getName();
  }

  @Override
  public void onUpdateReceived(Update update) {
    log.info("Received update {}", update);
  }

  @Override
  public String getBotUsername() {
    return name;
  }
}
