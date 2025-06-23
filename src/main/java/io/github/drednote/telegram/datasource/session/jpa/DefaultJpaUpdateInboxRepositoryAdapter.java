package io.github.drednote.telegram.datasource.session.jpa;

import io.github.drednote.telegram.core.request.ParsedUpdateRequest;
import io.github.drednote.telegram.datasource.session.UpdateInboxRepositoryAdapter;
import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.utils.Assert;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.MappingMetamodelImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class DefaultJpaUpdateInboxRepositoryAdapter implements UpdateInboxRepositoryAdapter<JpaUpdateInbox> {

    private static final Logger log = LoggerFactory.getLogger(DefaultJpaUpdateInboxRepositoryAdapter.class);

    protected final JpaUpdateInboxRepository repository;
    protected final EntityManager entityManager;
    protected final SessionProperties properties;

    protected DefaultJpaUpdateInboxRepositoryAdapter(
        JpaUpdateInboxRepository repository, EntityManager entityManager, SessionProperties properties
    ) {
        Assert.required(repository, "JpaUpdateInboxRepository");
        Assert.required(entityManager, "EntityManager");
        Assert.required(properties, "SessionProperties");

        this.repository = repository;
        this.entityManager = entityManager;
        this.properties = properties;
    }

    protected abstract Optional<JpaUpdateInbox> findWithMaxThreadsPerUser(int maxThreadsPerUser);

    @Override
    @Transactional
    public void persist(List<Update> updates) {
        for (Update update : updates) {
            ParsedUpdateRequest request = new ParsedUpdateRequest(update, null);
            JpaUpdateInbox jpaUpdateInbox = new JpaUpdateInbox();
            jpaUpdateInbox.setUpdateId(update.getUpdateId());
            jpaUpdateInbox.setUpdate(update);
            jpaUpdateInbox.setStatus(UpdateInboxStatus.NEW);
            jpaUpdateInbox.setEntityId(request.getUserId() != null ? request.getUserId().toString() : null);
            try {
                entityManager.persist(jpaUpdateInbox);
            } catch (DataIntegrityViolationException | PersistenceException e) {
                log.warn("Cannot persist update inbox with id '{}'. Cause: {}",
                    update.getUpdateId(), e.getMessage());
                log.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional
    public void update(JpaUpdateInbox updateInbox) {
        repository.save(updateInbox);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<JpaUpdateInbox> findNextUpdate() {
        int maxThreadsPerUser = properties.getMaxThreadsPerUser();
        Optional<JpaUpdateInbox> next;
        if (maxThreadsPerUser > 0) {
            next = findWithMaxThreadsPerUser(maxThreadsPerUser);
        } else {
            next = repository.findNextWithoutLimit();
        }

        return next.map(inbox -> {
            log.trace("Found next update inbox: {}", inbox.getUpdatedAt());
            if (inbox.getStatus() == UpdateInboxStatus.NEW) {
                inbox.setStatus(UpdateInboxStatus.IN_PROGRESS);
                repository.save(inbox);
            }
            return inbox;
        });
    }

    @Override
    @Transactional
    public void timeoutTasks() {
        Integer idleInterval = properties.getSchedulerProcessor().getIdleInterval();
        List<JpaUpdateInbox> idleEntities = repository.getIdleEntities(
            Instant.now().minus(idleInterval, ChronoUnit.MILLIS));
        if (!idleEntities.isEmpty()) {
            log.warn("Some updates from telegram are containing in status 'IN_PROGRESS' "
                     + "more than threshold '{}' milliseconds. You can change threshold parameter "
                     + "'drednote.telegram.session.scheduler-datasource-reader.idle-interval' if it too small",
                idleInterval);
            idleEntities.forEach(entity -> {
                entity.setStatus(UpdateInboxStatus.TIMEOUT);
                try {
                    repository.save(entity);
                } catch (Exception e) {
                    log.error("Cannot mark update inbox with id '{}' as idle", entity.getUpdateId(), e);
                }
            });
        }
    }

    protected String getTable() {
        SessionFactoryImplementor sessionFactory = entityManager.getEntityManagerFactory()
            .unwrap(SessionFactoryImplementor.class);
        MappingMetamodelImplementor metaModel = sessionFactory.getRuntimeMetamodels().getMappingMetamodel();
        return metaModel.getEntityDescriptor(JpaUpdateInbox.class).getMappedTableDetails().getTableName();
    }
}
