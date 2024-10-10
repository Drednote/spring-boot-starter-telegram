package io.github.drednote.telegram.session;

import io.github.drednote.telegram.utils.Assert;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * {@code AbstractTelegramUpdateProcessor} is an abstract class that implements the
 * {@code TelegramUpdateProcessor} interface for processing Telegram updates. It provides the
 * foundational functionalities for managing the processing of updates with a thread pool and limits
 * on the number of messages in queue.
 *
 * <p>This class handles the configuration of thread pool settings and ensures proper
 * synchronization when processing updates.</p>
 *
 * @author Ivan Galushko
 */
public abstract class AbstractTelegramUpdateProcessor implements TelegramUpdateProcessor {

    private static final Logger log = LoggerFactory.getLogger(
        AbstractTelegramUpdateProcessor.class);
    final ThreadPoolExecutor executorService;
    private final int maxMessageInQueue;

    private final ReadWriteLock maxMessagesLock = new ReentrantReadWriteLock();
    private final Condition maxMessagesLimit = maxMessagesLock.writeLock().newCondition();

    /**
     * Constructs an {@code AbstractTelegramUpdateProcessor} with specified properties and thread
     * factory.
     *
     * @param properties    configuration settings for the session.
     * @param threadFactory the factory to create threads for the executor service.
     * @throws IllegalArgumentException if {@code maxMessagesInQueue} is negative or
     *                                  {@code consumeMaxThreads} is not positive.
     */
    protected AbstractTelegramUpdateProcessor(
        SessionProperties properties, ThreadFactory threadFactory
    ) {
        Assert.required(properties, "SessionProperties");
        Assert.required(threadFactory, "ThreadFactory");

        int consumeMaxThreads = properties.getConsumeMaxThreads();
        int maxMessagesInQueue = properties.getMaxMessagesInQueue();
        if (maxMessagesInQueue < 0) {
            throw new IllegalArgumentException(
                "maxMessageInQueue must be greater than or equal to 0");
        }
        if (consumeMaxThreads <= 0) {
            throw new IllegalArgumentException("maxThreads must be greater than 0");
        }

        this.maxMessageInQueue = maxMessagesInQueue == 0 ? Integer.MAX_VALUE : maxMessagesInQueue;
        this.executorService = new ThreadPoolExecutor(
            consumeMaxThreads, consumeMaxThreads,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(this.maxMessageInQueue),
            threadFactory,
            new WaitUntilAvailablePolicy()
        );
    }

    /**
     * Constructs an {@code AbstractTelegramUpdateProcessor} with default thread factory.
     *
     * @param properties configuration settings for the session.
     */
    protected AbstractTelegramUpdateProcessor(SessionProperties properties) {
        this(properties, Executors.defaultThreadFactory());
    }

    /**
     * Processes a list of updates by submitting them to the executor service for handling.
     *
     * @param updates The list of updates to be processed
     */
    public synchronized void process(List<Update> updates) {
        for (Update update : updates) {
            getRunnable(update).ifPresent(runnable -> {
                executorService.submit(() -> {
                    try {
                        runnable.run();
                    } finally {
                        releaseSpace();
                    }
                });
            });
        }
    }

    /**
     * Returns an optional runnable task for the given update.
     *
     * @param update the Telegram update to be processed.
     * @return an {@code Optional<Runnable>} containing the task to execute or empty if not
     * applicable.
     */
    protected abstract Optional<Runnable> getRunnable(Update update);

    private void releaseSpace() {
        maxMessagesLock.readLock().lock();
        try {
            if (executorService.getQueue().size() >= maxMessageInQueue - 1) {
                maxMessagesLock.readLock().unlock();
                maxMessagesLock.writeLock().lock();
                try {
                    log.trace("Releasing waiting thread pool");
                    maxMessagesLimit.signal();
                    maxMessagesLock.readLock().lock();
                } finally {
                    maxMessagesLock.writeLock().unlock();
                }
            }
        } finally {
            maxMessagesLock.readLock().unlock();
        }
    }

    /**
     * {@code WaitUntilAvailablePolicy} is a custom rejection policy that handles rejected tasks by
     * waiting until there is space available in the executor's queue.
     */
    private class WaitUntilAvailablePolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.trace("Waiting until thread pool will be available");
            maxMessagesLock.writeLock().lock();
            try {
                while (!executor.getQueue().offer(r)) {
                    if (executor.isShutdown()) {
                        break;
                    }
                    maxMessagesLimit.await();
                }
                log.trace("Put task into queue");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RejectedExecutionException("Interrupted", e);
            } finally {
                maxMessagesLock.writeLock().unlock();
            }
        }
    }
}
