package io.github.drednote.telegram.session;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.github.drednote.telegram.core.TelegramBot;
import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.core.request.ParsedUpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterProperties;
import io.github.drednote.telegram.response.TooManyRequestsTelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.checkerframework.checker.index.qual.NonNegative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Default implementation of the {@link TelegramUpdateProcessor} interface for managing consumption
 * updates.
 * <p>
 * {@code DefaultTelegramUpdateProcessor} processes updates from Telegram. It handles user requests
 * and applies rate limiting based on the configuration provided.
 *
 * @author Ivan Galushko
 * @see TelegramBot
 * @see SessionProperties
 */
public class DefaultTelegramUpdateProcessor extends AbstractTelegramUpdateProcessor {

    private static final Logger log = LoggerFactory.getLogger(DefaultTelegramUpdateProcessor.class);
    private final TelegramBot telegramBot;
    private final int maxThreadsPerUser;
    private final Cache<Long, Semaphore> userProcessing;
    private final Set<UpdateRequest> limitProcessing;
    private final UserRateLimitRequestFilter userRateLimitRequestFilter;
    private final TelegramClient telegramClient;
    private final ReadWriteLock limitLock = new ReentrantReadWriteLock();
    @Nullable
    private final TelegramMessageSource messageSource;

    /**
     * Constructs a {@code DefaultTelegramUpdateProcessor} with specified properties and Telegram
     * client.
     *
     * @param properties       Configuration settings for the session.
     * @param filterProperties Filter properties for controlling request processing.
     * @param telegramBot      The telegram bot instance for sending messages.
     * @param telegramClient   The Telegram client instance used to interact with Telegram API.
     * @param threadFactory    The thread factory to create threads for processing requests.
     * @throws IllegalArgumentException if {@code maxThreadsPerUser} or {@code cacheLiveDuration} is
     *                                  invalid.
     */
    public DefaultTelegramUpdateProcessor(
        SessionProperties properties, FilterProperties filterProperties, TelegramBot telegramBot,
        TelegramClient telegramClient, ThreadFactory threadFactory,
        @Nullable TelegramMessageSource messageSource
    ) {
        super(properties, threadFactory);
        Assert.required(telegramBot, "TelegramBot");
        Assert.required(filterProperties, "FilterProperties");
        Assert.required(telegramClient, "TelegramClient");

        this.messageSource = messageSource;
        this.telegramClient = telegramClient;
        this.telegramBot = telegramBot;
        this.maxThreadsPerUser = properties.getMaxThreadsPerUser();

        if (maxThreadsPerUser < 0) {
            throw new IllegalArgumentException("maxThreadsPerUser must be greater than 0");
        }
        if (properties.getCacheLiveDuration() < 0) {
            throw new IllegalArgumentException(
                "cacheLiveDuration must be greater or equals than 0");
        }

        this.userProcessing = Caffeine.newBuilder()
            .expireAfter(new UserProcessingExpiry(
                properties.getCacheLiveDuration(),
                properties.getCacheLiveDurationUnit(),
                maxThreadsPerUser)
            )
            .build();
        this.limitProcessing = ConcurrentHashMap.newKeySet();
        this.userRateLimitRequestFilter = new UserRateLimitRequestFilter(filterProperties);
        ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleWithFixedDelay(
            this::sendRateLimitResponse, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Constructs a {@code DefaultTelegramUpdateProcessor} with default thread factory.
     *
     * @param properties       Configuration settings for the session.
     * @param filterProperties Filter properties for controlling request processing.
     * @param telegramBot      The telegram bot instance for sending messages.
     * @param telegramClient   The Telegram client instance used to interact with Telegram API.
     */
    public DefaultTelegramUpdateProcessor(
        SessionProperties properties, FilterProperties filterProperties, TelegramBot telegramBot,
        TelegramClient telegramClient, @Nullable TelegramMessageSource messageSource
    ) {
        this(properties, filterProperties, telegramBot, telegramClient,
            Executors.defaultThreadFactory(), messageSource);
    }

    /**
     * Retrieves a runnable task for processing the given update.
     *
     * @param update the Telegram update to be processed.
     * @return an {@code Optional<Runnable>} containing the task to execute or empty if rate limit
     * exceeds.
     */
    @Override
    protected Optional<Runnable> getRunnable(Update update) {
        ParsedUpdateRequest request = new ParsedUpdateRequest(update, telegramClient);
        User user = request.getUser();
        if (user == null) {
            return doProcessUpdate(update, null);
        } else if (maxThreadsPerUser == 0) {
            return processWithoutLimit(user.getId(), request);
        } else {
            return processWithLimitByUser(user.getId(), request);
        }
    }

    /**
     * Processes the update without any thread limit for the user.
     *
     * @param userId  the ID of the user.
     * @param request the UpdateRequest containing the update details.
     * @return an {@code Optional<Runnable>} containing the task to execute or empty if not
     * applicable.
     */
    private Optional<Runnable> processWithoutLimit(Long userId, UpdateRequest request) {
        Update update = request.getOrigin();
        boolean limitNotExceeded = userRateLimitRequestFilter.filter(userId);
        return limitNotExceeded
            ? doProcessUpdate(update, null)
            : addRateLimitResponseToProcess(request, userId);
    }

    /**
     * Processes the update applying user-specific thread limits.
     *
     * @param userId  the ID of the user.
     * @param request the UpdateRequest containing the update details.
     * @return an {@code Optional<Runnable>} containing the task to execute or empty if not
     * applicable.
     */
    private Optional<Runnable> processWithLimitByUser(Long userId, UpdateRequest request) {
        Semaphore semaphore = userProcessing.get(userId, k -> new Semaphore(maxThreadsPerUser));
        Update update = request.getOrigin();

        if (userRateLimitRequestFilter.filter(userId) && semaphore.tryAcquire()) {
            log.trace("Lock user {}", userId);
            return doProcessUpdate(update, semaphore::release);
        } else {
            return addRateLimitResponseToProcess(request, userId);
        }
    }

    /**
     * Processes the given update and defines an action to perform once processing is finished.
     *
     * @param update         the Telegram update to be processed.
     * @param actionOnFinish an optional runnable action to execute after processing is complete.
     * @return an {@code Optional<Runnable>} containing the task to execute for processing.
     */
    private Optional<Runnable> doProcessUpdate(Update update, @Nullable Runnable actionOnFinish) {
        return Optional.of(() -> {
            try {
                log.debug("Executing update {}", update.getUpdateId());
                telegramBot.onUpdateReceived(update);
            } finally {
                log.trace("Release update {}", update.getUpdateId());
                if (actionOnFinish != null) {
                    actionOnFinish.run();
                }
            }
        });
    }

    private Optional<Runnable> addRateLimitResponseToProcess(UpdateRequest request, Long userId) {
        limitLock.readLock().lock();
        try {
            log.trace("Limit exceeded for user {}", userId);
            limitProcessing.add(request);
        } finally {
            limitLock.readLock().unlock();
        }
        return Optional.empty();
    }

    /**
     * Processes and sends responses for requests that exceeded rate limits, clearing the limit
     * processing set.
     */
    private void sendRateLimitResponse() {
        limitLock.writeLock().lock();
        Set<UpdateRequest> updateRequests;
        try {
            updateRequests = new HashSet<>(limitProcessing);
            limitProcessing.clear();
        } finally {
            limitLock.writeLock().unlock();
        }
        updateRequests.iterator().forEachRemaining(request -> {
            try {
                TooManyRequestsTelegramResponse.INSTANCE.setMessageSource(messageSource);
                TooManyRequestsTelegramResponse.INSTANCE.process(request);
            } catch (Exception e) {
                log.error("Cannot process response to telegram for request {}", request, e);
            }
        });
    }

    /**
     * {@code UserProcessingExpiry} is responsible for determining the expiration time for user
     * processing semaphores.
     */
    private static class UserProcessingExpiry extends AbstractExpiry<Semaphore> {

        private final long duration;
        private final TimeUnit unit;
        private final int maxThreadsPerUser;

        /**
         * Constructs a {@code UserProcessingExpiry} with specified parameters.
         *
         * @param duration          the duration for which the semaphore is valid.
         * @param unit              the time unit of the duration.
         * @param maxThreadsPerUser the maximum number of threads allowed per user.
         */
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

    /**
     * {@code AbstractExpiry} provides a base implementation for expiry policies for caching
     * mechanisms.
     *
     * @param <T> the type of value associated with the key.
     */
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
