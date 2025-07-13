package io.github.drednote.telegram.datasource.session;

import io.github.drednote.telegram.core.request.ParsedUpdateRequest;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.utils.Assert;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Abstract base class for implementing {@link UpdateInboxRepositoryAdapter} backed by a repository.
 * <p>
 * Handles core logic for persisting, retrieving, and updating {@link UpdateInbox} entities. The strategy for selecting
 * and managing inbox records is delegated to abstract methods.
 * </p>
 *
 * <p>This class expects subclasses to define how to:
 * <ul>
 *   <li>Find idle entities for timeout processing</li>
 *   <li>Retrieve the next update based on concurrency limits</li>
 *   <li>Persist a single entity to the underlying storage</li>
 * </ul>
 * </p>
 *
 * <p>Entity creation is handled reflectively via {@link Class#getDeclaredConstructor(Class[])}}.</p>
 *
 * @param <T> the inbox entity type
 * @author Ivan Galushko
 */
public abstract class AbstractUpdateInboxRepositoryAdapter<T extends UpdateInbox>
    implements UpdateInboxRepositoryAdapter<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractUpdateInboxRepositoryAdapter.class);

    protected final SessionProperties properties;
    private final UpdateInboxRepository<T> repository;
    private final Class<T> clazz;

    /**
     * Constructs the adapter with required dependencies.
     *
     * @param properties configuration for session control and threading
     * @param repository the inbox repository implementation
     * @param clazz      the class type of the inbox entity
     */
    protected AbstractUpdateInboxRepositoryAdapter(
        SessionProperties properties, UpdateInboxRepository<T> repository, Class<T> clazz
    ) {
        Assert.required(repository, "JpaUpdateInboxRepository");
        Assert.required(properties, "SessionProperties");
        Assert.required(clazz, "Class");

        this.clazz = clazz;
        this.repository = repository;
        this.properties = properties;
    }

    /**
     * Must return a list of entities that have been in {@link UpdateInboxStatus#IN_PROGRESS} state longer than the
     * configured threshold.
     *
     * @param date cutoff instant for detecting idle entries
     * @return list of idle entities
     */
    protected abstract List<T> getIdleEntities(Instant date);

    /**
     * Retrieves the next update respecting the max threads per user limit.
     *
     * @param maxThreadsPerUser maximum number of simultaneous updates per user
     * @return optional inbox entity to process
     */
    protected abstract Optional<T> findWithMaxThreadsPerUser(int maxThreadsPerUser);

    /**
     * Retrieves the next available update without thread restrictions.
     *
     * @return optional inbox entity
     */
    protected abstract Optional<T> findNextWithoutLimit();

    /**
     * Persists the given inbox entity to the repository.
     *
     * @param entity the inbox entity to save
     */
    protected abstract void persist(T entity);

    @Override
    public void persist(List<Update> updates) {
        for (Update update : updates) {
            ParsedUpdateRequest request = new ParsedUpdateRequest(update, null);
            try {
                T jpaUpdateInbox = clazz.getDeclaredConstructor().newInstance();
                jpaUpdateInbox.setUpdateId(update.getUpdateId());
                jpaUpdateInbox.setUpdate(update);
                jpaUpdateInbox.setStatus(UpdateInboxStatus.NEW);
                jpaUpdateInbox.setEntityId(request.getUserId() != null ? request.getUserId().toString() : null);
                persist(jpaUpdateInbox);
            } catch (DataIntegrityViolationException e) {
                log.warn("Cannot persist update inbox with id '{}'. Cause: {}",
                    update.getUpdateId(), e.getMessage());
                log.debug(e.getMessage(), e);
            } catch (Exception e) {
                log.error("Cannot persist update inbox with id '{}'. Cause: ", update.getUpdateId(), e);
            }
        }
    }

    @Override
    public void update(T updateInbox) {
        repository.save(updateInbox);
    }

    @Override
    public Optional<T> findNextUpdate() {
        int maxThreadsPerUser = properties.getMaxThreadsPerUser();
        Optional<T> next;
        if (maxThreadsPerUser > 0) {
            next = findWithMaxThreadsPerUser(maxThreadsPerUser);
        } else {
            next = findNextWithoutLimit();
        }

        return next.map(inbox -> {
            log.trace("Found next update inbox: {}", inbox.getUpdateId());
            if (inbox.getStatus() == UpdateInboxStatus.NEW) {
                inbox.setStatus(UpdateInboxStatus.IN_PROGRESS);
                update(inbox);
            }
            return inbox;
        });
    }

    @Override
    public void timeoutTasks() {
        int idleInterval = properties.getSchedulerProcessor().getIdleInterval();
        List<T> idleEntities = getIdleEntities(
            Instant.now().minus(idleInterval, ChronoUnit.MILLIS));
        if (!idleEntities.isEmpty()) {
            log.warn("Some updates from telegram are containing in status 'IN_PROGRESS' "
                     + "more than threshold '{}' milliseconds. You can change threshold parameter "
                     + "'drednote.telegram.session.scheduler-datasource-reader.idle-interval' if it too small",
                idleInterval);
            idleEntities.forEach(entity -> {
                entity.setStatus(UpdateInboxStatus.TIMEOUT);
                try {
                    update(entity);
                } catch (Exception e) {
                    log.error("Cannot mark update inbox with id '{}' as idle", entity.getUpdateId(), e);
                }
            });
        }
    }
}
