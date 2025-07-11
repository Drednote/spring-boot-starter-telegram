package io.github.drednote.telegram.datasource.session.jpa;

import io.github.drednote.telegram.datasource.session.AbstractUpdateInboxRepositoryAdapter;
import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.utils.Assert;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.time.Instant;
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

public abstract class DefaultJpaUpdateInboxRepositoryAdapter extends
    AbstractUpdateInboxRepositoryAdapter<JpaUpdateInbox> {

    private static final Logger log = LoggerFactory.getLogger(DefaultJpaUpdateInboxRepositoryAdapter.class);
    protected final JpaUpdateInboxRepository repository;
    protected final EntityManager entityManager;
    protected final SessionProperties properties;

    protected DefaultJpaUpdateInboxRepositoryAdapter(
        JpaUpdateInboxRepository repository, EntityManager entityManager, SessionProperties properties
    ) {
        super(properties, repository, JpaUpdateInbox.class);
        Assert.required(entityManager, "EntityManager");

        this.repository = repository;
        this.entityManager = entityManager;
        this.properties = properties;
    }


    @Override
    protected List<JpaUpdateInbox> getIdleEntities(Instant date) {
        return repository.getIdleEntities(date);
    }

    @Override
    protected Optional<JpaUpdateInbox> findNextWithoutLimit() {
        return repository.findNextWithoutLimit();
    }

    @Override
    protected void persist(JpaUpdateInbox entity) {
        try {
            entityManager.persist(entity);
        } catch (PersistenceException e) {
            log.warn("Cannot persist update inbox with id '{}'. Cause: {}",
                entity.getUpdateId(), e.getMessage());
            log.debug(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void persist(List<Update> updates) {
        super.persist(updates);
    }

    @Override
    @Transactional
    public void update(JpaUpdateInbox updateInbox) {
        super.update(updateInbox);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<JpaUpdateInbox> findNextUpdate() {
        return super.findNextUpdate();
    }

    @Override
    @Transactional
    public void timeoutTasks() {
        super.timeoutTasks();
    }

    protected String getTable() {
        SessionFactoryImplementor sessionFactory = entityManager.getEntityManagerFactory()
            .unwrap(SessionFactoryImplementor.class);
        MappingMetamodelImplementor metaModel = sessionFactory.getRuntimeMetamodels().getMappingMetamodel();
        return metaModel.getEntityDescriptor(JpaUpdateInbox.class).getMappedTableDetails().getTableName();
    }
}
