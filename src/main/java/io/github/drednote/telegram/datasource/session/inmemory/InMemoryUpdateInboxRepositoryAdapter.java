package io.github.drednote.telegram.datasource.session.inmemory;

import static io.github.drednote.telegram.datasource.session.UpdateInboxStatus.ERROR;
import static io.github.drednote.telegram.datasource.session.UpdateInboxStatus.PROCESSED;

import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.core.request.ParsedUpdateRequest;
import io.github.drednote.telegram.datasource.session.UpdateInboxRepositoryAdapter;
import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import io.github.drednote.telegram.exception.type.SessionTelegramException;
import io.github.drednote.telegram.response.TooManyRequestsTelegramResponse;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.utils.Assert;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * This implementation uses {@link LinkedBlockingQueue} as a place to save incoming messages. This approach is safe in terms of
 * multithreading, but it has a number of disadvantages. If the service crashes while processing messages, then the
 * messages will be lost. Also, if some user decides to spam a huge number of messages, there may be a situation where
 * the entire queue will be clogged with his messages, and they will block the processing of messages from other users.
 * <p>
 * If this happens, you might want to adjust the {@code drednote.telegram.session.maxMessageInQueuePerUser} parameter.
 * But this will slow down the writing of messages to the queue.
 *
 * @author Ivan Galushko
 */
public class InMemoryUpdateInboxRepositoryAdapter implements UpdateInboxRepositoryAdapter<InMemoryUpdateInbox> {

    private static final Logger log = LoggerFactory.getLogger(InMemoryUpdateInboxRepositoryAdapter.class);

    private final LinkedBlockingQueue<InMemoryUpdateInbox> queue;
    private final Lock lock;

    private final int maxThreadsPerUser;
    private final int maxMessageInQueuePerUser;
    private final TelegramMessageSource messageSource;

    public InMemoryUpdateInboxRepositoryAdapter(SessionProperties sessionProperties, TelegramMessageSource messageSource) {
        Assert.required(sessionProperties, "SessionProperties");
        Assert.required(messageSource, "TelegramMessageSource");

        this.messageSource = messageSource;
        int maxMessagesInQueue =
            sessionProperties.getMaxMessagesInQueue() <= 0 ? Integer.MAX_VALUE
                : sessionProperties.getMaxMessagesInQueue();
        this.maxThreadsPerUser = sessionProperties.getMaxThreadsPerUser();
        this.maxMessageInQueuePerUser = sessionProperties.getSchedulerProcessor().getMaxMessageInQueuePerUser();

        if (maxMessageInQueuePerUser < 0) {
            throw new IllegalArgumentException("maxMessageInQueuePerUser must be greater than or equal to 0");
        }
        if (maxThreadsPerUser < 0) {
            throw new IllegalArgumentException("maxThreadsPerUser must be greater than or equal to 0");
        }

        this.queue = new LinkedBlockingQueue<>(maxMessagesInQueue);
        this.lock = new ReentrantLock();
    }

    @Override
    public void persist(List<Update> updates) {
        for (Update update : updates) {
            ParsedUpdateRequest request = new ParsedUpdateRequest(update, null);
            InMemoryUpdateInbox inMemoryUpdateInbox = new InMemoryUpdateInbox();
            inMemoryUpdateInbox.setUpdateId(update.getUpdateId());
            inMemoryUpdateInbox.setUpdate(update);
            inMemoryUpdateInbox.setStatus(UpdateInboxStatus.NEW);
            String entityId = request.getUserId() != null ? request.getUserId().toString() : null;
            inMemoryUpdateInbox.setEntityId(entityId);
            try {
                if (maxMessageInQueuePerUser > 0 && entityId != null) {
                    long countInQueue = queue.stream().filter(el -> entityId.equals(el.getEntityId())).count();
                    if (countInQueue > maxMessageInQueuePerUser) {
                        TooManyRequestsTelegramResponse response = new TooManyRequestsTelegramResponse();
                        response.setMessageSource(messageSource);
                        response.process(request);
                        continue;
                    }
                }
                queue.put(inMemoryUpdateInbox);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SessionTelegramException("Error while persisting update inbox", e);
            } catch (Exception e) {
                throw new SessionTelegramException("Error while persisting update inbox", e);
            }
        }
    }

    @Override
    public void update(InMemoryUpdateInbox updateInbox) {
        if (updateInbox.getStatus() == PROCESSED || updateInbox.getStatus() == ERROR) {
            log.debug("Update {} finished with {} status", updateInbox.getUpdateId(), updateInbox.getStatus());
            boolean remove = queue.remove(updateInbox);
            if (!remove) {
                log.warn("Something went wrong. Update {} already processed", updateInbox.getUpdateId());
            }
        }
    }

    @Override
    public Optional<InMemoryUpdateInbox> findNextUpdate() {
        try {
            lock.lockInterruptibly();
            Optional<InMemoryUpdateInbox> result = queue.stream()
                .filter(el -> el.getStatus() == UpdateInboxStatus.NEW)
                .filter(el -> {
                    String entityId = el.getEntityId();
                    if (entityId == null || maxThreadsPerUser == 0) {
                        return true;
                    }

                    return queue.stream()
                               .filter(subEl -> entityId.equals(subEl.getEntityId()))
                               .filter(subEL -> subEL.getStatus() == UpdateInboxStatus.IN_PROGRESS)
                               .count() < maxThreadsPerUser;
                })
                .findFirst();
            result.ifPresent(inMemoryUpdateInbox -> inMemoryUpdateInbox.setStatus(UpdateInboxStatus.IN_PROGRESS));
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SessionTelegramException("Error while finding next update", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void timeoutTasks() {
        // nothing to do
    }
}
