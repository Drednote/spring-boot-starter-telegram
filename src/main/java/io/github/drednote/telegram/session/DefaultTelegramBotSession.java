package io.github.drednote.telegram.session;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.github.drednote.telegram.core.TelegramBot;
import io.github.drednote.telegram.core.request.ParsedUpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.checkerframework.checker.index.qual.NonNegative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Default implementation of the {@link TelegramBotSession} interface for managing consumption
 * updates.
 *
 * <p>Implementations should call {@link DefaultTelegramBotSession#processUpdates(List)} for sending
 * updates to {@link TelegramBot}
 *
 * @author Ivan Galushko
 * @see TelegramBot
 * @see SessionProperties
 */
public abstract class DefaultTelegramBotSession implements TelegramBotSession {

    private static final Logger log = LoggerFactory.getLogger(DefaultTelegramBotSession.class);
    private final TelegramBot telegramBot;
    private final ExecutorService executorService;
    private final int maxThreadsPerUser;
    private final int maxMessageInQueue;
    final AtomicInteger updatesCount = new AtomicInteger(0);
    private final Cache<Long, Queue<Update>> updates;
    private final Cache<Long, Semaphore> userProcessing;

    private final ReadWriteLock maxMessagesLock = new ReentrantReadWriteLock();
    private final Condition maxMessagesLimit = maxMessagesLock.writeLock().newCondition();

    protected DefaultTelegramBotSession(
        SessionProperties properties, TelegramBot telegramBot
    ) {
        Assert.required(telegramBot, "TelegramBot");
        Assert.required(properties, "SessionProperties");

        this.telegramBot = telegramBot;
        this.maxThreadsPerUser = properties.getMaxThreadsPerUser();
        this.maxMessageInQueue = properties.getLongPolling().getMaxMessagesInQueue();

        if (properties.getConsumeMaxThreads() <= 0) {
            throw new IllegalArgumentException("maxThreads must be greater than 0");
        }
        if (maxThreadsPerUser < 0) {
            throw new IllegalArgumentException("maxThreadsPerUser must be greater than 0");
        }
        if (maxMessageInQueue < 0) {
            throw new IllegalArgumentException(
                "maxMessageInQueue must be greater or equals than 0");
        }
        if (properties.getCacheLiveDuration() < 0) {
            throw new IllegalArgumentException(
                "cacheLiveDuration must be greater or equals than 0");
        }

        this.executorService = Executors.newFixedThreadPool(properties.getConsumeMaxThreads());
        this.updates = Caffeine.newBuilder()
            .expireAfter(new UpdatesExpiry(
                properties.getCacheLiveDuration(),
                properties.getCacheLiveDurationUnit())
            )
            .build();
        this.userProcessing = Caffeine.newBuilder()
            .expireAfter(new UserProcessingExpiry(
                properties.getCacheLiveDuration(),
                properties.getCacheLiveDurationUnit(), maxThreadsPerUser)
            )
            .build();
    }

    /**
     * Processes a list of updates by submitting them to the executor service for handling.
     *
     * @param updates The list of updates to be processed
     */
    protected synchronized void processUpdates(List<Update> updates) {
        for (Update update : updates) {
            waitFreeSpaceIfNeeded();
            if (maxThreadsPerUser == 0) {
                executorService.submit(() -> telegramBot.onUpdateReceived(update));
            } else {
                ParsedUpdateRequest request = new ParsedUpdateRequest(update);
                User user = request.getUser();
                if (user != null) {
                    processWithLimitByUser(update, user.getId());
                } else {
                    executorService.submit(() -> telegramBot.onUpdateReceived(update));
                }
            }
        }
    }

    private void waitFreeSpaceIfNeeded() {
        maxMessagesLock.readLock().lock();
        try {
            if (updatesCount.get() >= maxMessageInQueue) {
                maxMessagesLock.readLock().unlock();
                maxMessagesLock.writeLock().lock();
                try {
                    while (updatesCount.get() >= maxMessageInQueue) {
                        maxMessagesLimit.await();
                    }
                    maxMessagesLock.readLock().lock();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    maxMessagesLock.writeLock().unlock();
                }
            }
        } finally {
            maxMessagesLock.readLock().unlock();
        }
    }

    private void processWithLimitByUser(Update update, Long userId) {
        log.trace("Saving to queue update {} with user {}", update.getUpdateId(), userId);
        updates.get(userId, k -> new ConcurrentLinkedQueue<>()).add(update);
        updatesCount.incrementAndGet();
        userProcessing.get(userId, k -> new Semaphore(maxThreadsPerUser));
        processNext(userId);
    }

    private void processNext(Long userId) {
        Semaphore semaphore = userProcessing.getIfPresent(userId);

        if (semaphore.tryAcquire()) {
            log.trace("Lock user {}", userId);
            Queue<Update> queue = updates.getIfPresent(userId);
            Update update = queue.poll();

            if (update != null) {
                executorService.submit(() -> {
                    try {
                        log.debug("Executing update {} with user {}", update.getUpdateId(), userId);
                        telegramBot.onUpdateReceived(update);
                    } finally {
                        releaseSpace();
                        semaphore.release();
                        log.trace("Release user {}", userId);
                        processNext(userId);
                    }
                });
            } else {
                semaphore.release();
            }
        }
    }

    private void releaseSpace() {
        maxMessagesLock.readLock().lock();
        try {
            if (updatesCount.get() >= maxMessageInQueue) {
                maxMessagesLock.readLock().unlock();
                maxMessagesLock.writeLock().lock();
                try {
                    if (updatesCount.getAndDecrement() >= maxMessageInQueue) {
                        maxMessagesLimit.signalAll();
                    }
                    maxMessagesLock.readLock().lock();
                } finally {
                    maxMessagesLock.writeLock().unlock();
                }
            } else {
                updatesCount.decrementAndGet();
            }
        } finally {
            maxMessagesLock.readLock().unlock();
        }
    }

    private static class UpdatesExpiry extends AbstractExpiry<Queue<Update>> {

        private final long duration;
        private final TimeUnit unit;

        private UpdatesExpiry(long duration, TimeUnit unit) {
            this.duration = duration;
            this.unit = unit;
        }

        protected long getDuration(Queue<Update> updates) {
            return updates.isEmpty() ? unit.toNanos(duration) : Long.MAX_VALUE;
        }
    }

    private static class UserProcessingExpiry extends AbstractExpiry<Semaphore> {

        private final long duration;
        private final TimeUnit unit;
        private final int maxThreadsPerUser;

        private UserProcessingExpiry(long duration, TimeUnit unit, int maxThreadsPerUser) {
            this.duration = duration;
            this.unit = unit;
            this.maxThreadsPerUser = maxThreadsPerUser;
        }

        protected long getDuration(Semaphore semaphore) {
            return semaphore.availablePermits() == maxThreadsPerUser
                ? unit.toNanos(duration)
                : Long.MAX_VALUE;
        }
    }

    private static abstract class AbstractExpiry<T> implements Expiry<Long, T> {

        @Override
        public long expireAfterCreate(Long key, T value, long currentTime) {
            return getDuration(value);
        }

        @Override
        public long expireAfterUpdate(Long key, T value, long currentTime,
            @NonNegative long currentDuration) {
            return getDuration(value);
        }

        @Override
        public long expireAfterRead(Long key, T value, long currentTime,
            @NonNegative long currentDuration) {
            return getDuration(value);
        }

        protected abstract long getDuration(T updates);
    }
}
