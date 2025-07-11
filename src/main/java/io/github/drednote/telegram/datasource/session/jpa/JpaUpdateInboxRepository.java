package io.github.drednote.telegram.datasource.session.jpa;

import io.github.drednote.telegram.datasource.session.UpdateInboxRepository;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public interface JpaUpdateInboxRepository
    extends UpdateInboxRepository<JpaUpdateInbox>, JpaRepository<JpaUpdateInbox, Integer> {

    @Query(value = """
            SELECT m FROM JpaUpdateInbox m
                WHERE m.status = 'NEW'
                ORDER BY m.createdAt
                LIMIT 1
        """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2") // skip locked
    })
    Optional<JpaUpdateInbox> findNextWithoutLimit();

    @Query(value = """
            SELECT m FROM JpaUpdateInbox m
                WHERE m.entityId = null or m.entityId = (SELECT m.entityId FROM JpaUpdateInbox m
                WHERE m.status = 'NEW'
                  AND (select count(m2)
                       FROM JpaUpdateInbox m2
                       WHERE m2.entityId = m.entityId
                         AND m2.status = 'IN_PROGRESS') < :count
                ORDER BY m.createdAt
                LIMIT 1)
        """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2") // skip locked
    })
    List<JpaUpdateInbox> findNextWithLimit(Integer count);

    @Query(value = """
             select m from JpaUpdateInbox m\s
             where m.status = 'IN_PROGRESS'
             and m.updatedAt < ?1
        \s""")
    @Transactional
    List<JpaUpdateInbox> getIdleEntities(Instant idleTime);
}
