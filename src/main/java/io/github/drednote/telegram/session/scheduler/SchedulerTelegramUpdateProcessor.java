package io.github.drednote.telegram.session.scheduler;

import io.github.drednote.telegram.core.TelegramBot;
import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.core.request.ParsedUpdateRequest;
import io.github.drednote.telegram.datasource.session.UpdateInbox;
import io.github.drednote.telegram.datasource.session.UpdateInboxRepositoryAdapter;
import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.response.TooManyRequestsTelegramResponse;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.session.TelegramUpdateProcessor;
import io.github.drednote.telegram.session.UserRateLimitRequestFilter;
import io.github.drednote.telegram.utils.Assert;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class SchedulerTelegramUpdateProcessor<T extends UpdateInbox>
    implements TelegramUpdateReader, TelegramUpdateProcessor {

    private static final Logger log = LoggerFactory.getLogger(SchedulerTelegramUpdateProcessor.class);

    private final TelegramBot telegramBot;
    private final UpdateInboxRepositoryAdapter<T> adapter;
    private final ThreadPoolExecutor executor;
    private final SessionProperties sessionProperties;
    private final SchedulerTelegramUpdateProcessorProperties readerProperties;
    private final ScheduledThreadPoolExecutor scheduledExecutor;
    private final AtomicBoolean isTaskRunning = new AtomicBoolean(false);
    private final UserRateLimitRequestFilter userRateLimitRequestFilter;
    private final TelegramClient telegramClient;
    private final TelegramMessageSource messageSource;

    private int delay;
    private boolean running = false;

    public SchedulerTelegramUpdateProcessor(
        TelegramBot telegramBot, UpdateInboxRepositoryAdapter<T> adapter, SessionProperties properties,
        FilterProperties filterProperties, TelegramClient telegramClient, TelegramMessageSource messageSource
    ) {
        Assert.required(telegramBot, "TelegramBot");
        Assert.required(adapter, "UpdateInboxRepositoryAdapter");
        Assert.required(properties, "SessionProperties");
        Assert.required(filterProperties, "FilterProperties");
        Assert.required(telegramClient, "TelegramClient");
        Assert.required(messageSource, "TelegramMessageSource");

        this.messageSource = messageSource;
        this.telegramClient = telegramClient;
        this.adapter = adapter;
        this.sessionProperties = properties;
        this.readerProperties = properties.getSchedulerProcessor();

        Assert.required(this.readerProperties, "SchedulerTelegramUpdateProcessorProperties");

        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(properties.getConsumeMaxThreads());
        this.telegramBot = telegramBot;
        this.scheduledExecutor = new ScheduledThreadPoolExecutor(1);
        this.delay = readerProperties.getMaxInterval();
        this.userRateLimitRequestFilter = new UserRateLimitRequestFilter(filterProperties);

        new ScheduledThreadPoolExecutor(1)
            .scheduleWithFixedDelay(new IdleScheduleTask(), readerProperties.getCheckIdleInterval(),
                readerProperties.getCheckIdleInterval(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void process(List<Update> updates) {
        adapter.persist(updates);
        readImmediately();
    }

    @Override
    public synchronized void start() {
        if (!running) {
            readImmediately();
            running = true;
            log.info("Started SchedulerTelegramUpdateProcessor");
        } else {
            throw new IllegalStateException("Reader already running");
        }
    }

    @Override
    public synchronized void stop() {
        if (running) {
            scheduledExecutor.shutdown();
            log.info("Stopped SchedulerTelegramUpdateProcessor");
        } else {
            throw new IllegalStateException("Reader already stopped");
        }
    }

    @Override
    public void readImmediately() {
        scheduledExecutor.schedule(new ScheduleTask(), 0, TimeUnit.MILLISECONDS);
    }

    public void read() {
        if (executor.getQueue().size() < sessionProperties.getConsumeMaxThreads()) {
            Optional<T> nextEntity = adapter.findNextUpdate();
            if (nextEntity.isPresent()) {
                T t = nextEntity.get();
                log.trace("Telegram update found: {}", t.getUpdateId());
                executor.submit(() -> {
                    try {
                        execute(t);
                    } catch (Exception e) {
                        log.error("An unhandled error occurred while processing the telegram update", e);
                    }
                });
                delay = Math.max(delay - readerProperties.getReducingIntervalAmount(),
                    readerProperties.getMinInterval());
            } else {
                delay = Math.min(delay + readerProperties.getIncreasingIntervalAmount(),
                    readerProperties.getMaxInterval());
            }
        } else {
            delay = readerProperties.getWaitInterval();
        }
    }

    private void execute(T entity) {
        try {
            doExecute(entity);
            entity.setStatus(UpdateInboxStatus.PROCESSED);
        } catch (Exception e) {
            log.error("An unhandled error occurred while processing the telegram update {}", entity.getUpdateId(), e);
            entity.setStatus(UpdateInboxStatus.ERROR);
            entity.setErrorDescription(e.getMessage());
        } finally {
            adapter.update(entity);
            log.debug("Telegram update processing complete: {}", entity.getUpdateId());
        }
    }

    private void doExecute(T updateInbox) {
        ParsedUpdateRequest request = new ParsedUpdateRequest(updateInbox.getUpdate(), telegramClient);
        User user = request.getUser();
        if (user != null && !userRateLimitRequestFilter.filter(user.getId())) {
            try {
                TooManyRequestsTelegramResponse response = new TooManyRequestsTelegramResponse();
                response.setMessageSource(messageSource);
                response.process(request);
            } catch (Exception e) {
                log.error("Cannot process response to telegram for request {}", request, e);
            }
        } else {
            telegramBot.onUpdateReceived(updateInbox.getUpdate());
        }
    }

    /**
     * Task for checking idle messages.
     */
    private class IdleScheduleTask implements Runnable {

        @Override
        public void run() {
            try {
                adapter.timeoutTasks();
            } catch (Exception e) {
                log.error("An unhandled error occurred while checking idle telegram updates", e);
            }
        }
    }

    /**
     * Task for processing messages.
     */
    private class ScheduleTask implements Runnable {

        @Override
        public void run() {
            if (!isTaskRunning.compareAndSet(false, true)) {
                return;
            }
            try {
                read();
            } catch (Exception e) {
                log.error("An unhandled error occurred while processing the telegram update", e);
            } finally {
                isTaskRunning.set(false);
                scheduledExecutor.schedule(this, delay, TimeUnit.MILLISECONDS);
            }
        }
    }
}
