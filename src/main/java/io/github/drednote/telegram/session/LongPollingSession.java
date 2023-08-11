package io.github.drednote.telegram.session;

import io.github.drednote.telegram.session.TelegramClient.Response;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.TelegramBot;

@Slf4j
public class LongPollingSession implements TelegramBotSession, Runnable {

  private final ScheduledExecutorService readerService;
  private final ExecutorService executorService;
  private final TelegramClient telegramClient;
  private final AtomicBoolean running = new AtomicBoolean(false);

  private LongPollingBot callback;
  private DefaultBotOptions options;
  private int lastReceivedUpdate = 0;

  public LongPollingSession(TelegramClient telegramClient, SessionProperties properties) {
    this.readerService = Executors.newSingleThreadScheduledExecutor();
    this.executorService = Executors.newFixedThreadPool(properties.getMaxThreads());
    this.telegramClient = telegramClient;
  }

  public synchronized void start() {
    if (running.get()) {
      throw new IllegalStateException("Session already running");
    }
    if (callback == null) {
      throw new IllegalStateException("Before start session, set callback");
    }

    readerService.scheduleWithFixedDelay(this, 0, 1, TimeUnit.MILLISECONDS);
    running.set(true);

    log.info("Started listen messages");
  }

  public synchronized void stop() {
    if (running.get()) {
      readerService.shutdown();

      if (callback != null) {
        callback.onClosing();
      }

      running.set(false);
    }
  }

  @Override
  public void run() {
    try {
      List<Update> updates = getUpdatesFromServer();
      log.trace("Updates size %s".formatted(updates.size()));
      if (!updates.isEmpty()) {
        updates.removeIf(x -> x.getUpdateId() < lastReceivedUpdate);
        lastReceivedUpdate = updates.stream()
            .map(Update::getUpdateId)
            .max(Integer::compareTo)
            .orElse(0);
        processUpdates(updates);
      }
    } catch (Exception global) {
      log.error(global.getLocalizedMessage(), global);
    }
  }

  protected void processUpdates(List<Update> updates) {
    updates.forEach(update -> executorService.submit(() -> callback.onUpdateReceived(update)));
  }

  private List<Update> getUpdatesFromServer() {
    try {
      log.trace("Started request");
      Response response = telegramClient.getUpdates(
          callback.getBotToken(),
          lastReceivedUpdate + 1,
          options.getGetUpdatesLimit(),
          options.getGetUpdatesTimeout(),
          options.getAllowedUpdates()
      );
      log.trace("Finished request");
      if (response.isOk()) {
        return response.getResult();
      } else {
        log.error("Something went wrong while trying to get updates, response = '%s'"
            .formatted(response));
      }
    } catch (Exception exception) {
      log.error("Error while reading updates", exception);
      try {
        Thread.sleep(options.getBackOff().nextBackOffMillis());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    return Collections.emptyList();
  }

  @Override
  public void setCallback(TelegramBot callback) {
    if (callback instanceof LongPollingBot longPollingBot) {
      this.callback = longPollingBot;
      if (longPollingBot.getOptions() instanceof DefaultBotOptions defaultBotOptions) {
        this.options = defaultBotOptions;
      } else {
        throw new UnsupportedOperationException("This session supports only DefaultBotOptions");
      }
    } else {
      throw new UnsupportedOperationException("This session supports only longPollingBot");
    }
  }
}
