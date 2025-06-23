package io.github.drednote.telegram.datasource.session.jpa;

import io.github.drednote.telegram.session.SessionProperties;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.Nullable;

public class PostgresUpdateInboxRepositoryAdapter extends DefaultJpaUpdateInboxRepositoryAdapter {

    @Nullable
    private String sql;

    public PostgresUpdateInboxRepositoryAdapter(
        JpaUpdateInboxRepository repository, EntityManager entityManager, SessionProperties sessionProperties
    ) {
        super(repository, entityManager,sessionProperties);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Optional<JpaUpdateInbox> findWithMaxThreadsPerUser(int maxThreadsPerUser) {
        String sql = getSql();
        Query query = entityManager.createNativeQuery(sql, JpaUpdateInbox.class);
        query.setParameter("count", maxThreadsPerUser);
        List<JpaUpdateInbox> resultList = query.getResultList();

        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    private String getSql() {
        if (sql == null) {
            String table = getTable();
            sql = """
                WITH Selected AS (SELECT m.*
                                     FROM update_inbox m
                                     WHERE m.status = 'NEW'
                                       AND (select count(*)
                                            FROM update_inbox m2
                                            WHERE m2.entity_id = m.entity_id
                                              AND m2.status = 'IN_PROGRESS') < :count
                                     ORDER BY m.entity_id, m.created_at
                                     LIMIT 1 FOR UPDATE SKIP LOCKED)
                   UPDATE update_inbox
                   SET status = 'IN_PROGRESS'
                   WHERE update_id = (SELECT update_id FROM Selected)
                   RETURNING *
                """.replace("$table", table);
        }
        return sql;
    }
}
