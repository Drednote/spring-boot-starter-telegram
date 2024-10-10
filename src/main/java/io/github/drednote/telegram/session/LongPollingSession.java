package io.github.drednote.telegram.session;

import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.interfaces.BackOff;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Implementation of the {@link TelegramBotSession} interface for managing a long polling session
 * with the Telegram Bot API.
 *
 * <p>This class implements the {@link TelegramBotSession} interface to provide methods for
 * starting and stopping a long polling session with the Telegram Bot API. It utilizes a
 * {@link TelegramClient} to fetch updates from the Telegram server and processes them using a
 * {@link TelegramUpdateProcessor}.
 *
 * <p>The class is responsible for scheduling and executing the polling loop, processing updates,
 * and handling exceptions that may occur during the session and not caught with exception handling
 * strategy.
 *
 * @author Ivan Galushko
 * @see TelegramClient
 * @see TelegramUpdateProcessor
 * @see SessionProperties
 */
public class LongPollingSession implements TelegramBotSession, Runnable {

    private static final Logger log = LoggerFactory.getLogger(LongPollingSession.class);

    private final ScheduledExecutorService readerService;
    private final TelegramClient telegramClient;
    private final AtomicBoolean running = new AtomicBoolean(false);
    protected final SessionProperties sessionProperties;
    protected final TelegramProperties telegramProperties;
    private final BackOff backOff;
    private final TelegramUpdateProcessor processor;

    private int lastReceivedUpdate = 0;

    /**
     * Constructs a LongPollingSession with the provided Telegram client, session properties, and
     * long polling bot callback.
     *
     * <p>The provided Telegram client is used to fetch updates from the Telegram server. The
     * session properties define bot configuration. The callback represents the long polling bot
     * that will handle received updates.
     *
     * @param telegramClient The Telegram client for fetching updates
     * @param properties     The session properties containing bot configuration
     * @throws UnsupportedOperationException If the callback is not an instance of
     *                                       {@code DefaultTelegramBot} or if the options of the
     *                                       callback are not {@code DefaultBotOptions}
     */
    public LongPollingSession(
        TelegramClient telegramClient, SessionProperties properties,
        TelegramProperties telegramProperties, BackOff backOff,
        TelegramUpdateProcessor processor
    ) {
        Assert.required(telegramClient, "TelegramClient");
        Assert.required(properties, "SessionProperties");
        Assert.required(telegramProperties, "TelegramProperties");
        Assert.required(backOff, "BackOff");
        Assert.required(processor, "TelegramUpdateProcessor");

        this.processor = processor;
        this.backOff = backOff;
        this.telegramProperties = telegramProperties;
        this.sessionProperties = properties;
        this.readerService = Executors.newSingleThreadScheduledExecutor();
        this.telegramClient = telegramClient;
    }

    /**
     * Starts the long polling session.
     *
     * <p>This method schedules the polling loop to fetch updates from the Telegram server and
     * process them using the provided callback.
     *
     * @throws IllegalStateException If the session is already running
     */
    public synchronized void start() {
        if (running.get()) {
            throw new IllegalStateException("Session already running");
        }

        readerService.scheduleWithFixedDelay(this, 0, 1, TimeUnit.MILLISECONDS);
        running.set(true);

        log.info("Started listen messages");
    }

    /**
     * Stops the long polling session.
     *
     * <p>This method shuts down the reader service and invokes the onClosing method of the
     * callback to perform any necessary cleanup.
     */
    public synchronized void stop() {
        if (running.get()) {
            readerService.shutdown();

            running.set(false);
        }
    }

    /**
     * The main loop of the polling session.
     *
     * <p>This method is executed periodically to fetch updates from the Telegram server. It
     * processes the updates and forwards them to the callback for handling.
     */
    @Override
    public void run() {
        try {
            List<Update> updates = getUpdatesFromServer();
            log.trace("Updates size {}", updates.size());
            if (!updates.isEmpty()) {
                updates.removeIf(x -> x.getUpdateId() < lastReceivedUpdate);
                lastReceivedUpdate = updates.stream()
                    .map(Update::getUpdateId)
                    .max(Integer::compareTo)
                    .orElse(0);
                processor.process(updates);
            }
        } catch (Exception global) {
            log.error(global.getLocalizedMessage(), global);
        }
    }

    private List<Update> getUpdatesFromServer() {
        try {
            log.trace("Started request");
            UpdateResponse response = telegramClient.getUpdates(
                telegramProperties.getToken(),
                lastReceivedUpdate + 1,
                sessionProperties.getLongPolling().getUpdateLimit(),
                sessionProperties.getLongPolling().getUpdateTimeout(),
                sessionProperties.getLongPolling().getAllowedUpdates()
            );
            if (!response.isOk()) {
                throw new TelegramApiException("Telegram api return not ok " + response);
            }
            backOff.reset();
            log.trace("Finished request");
            return response.getResult();
        } catch (Exception exception) {
            log.error("Error while reading updates", exception);
            try {
                Thread.sleep(backOff.nextBackOffMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return Collections.emptyList();
    }
}
