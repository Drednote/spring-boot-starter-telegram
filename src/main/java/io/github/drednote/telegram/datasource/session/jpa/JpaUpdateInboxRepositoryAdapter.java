package io.github.drednote.telegram.datasource.session.jpa;

import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import io.github.drednote.telegram.session.SessionProperties;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class JpaUpdateInboxRepositoryAdapter extends DefaultJpaUpdateInboxRepositoryAdapter {

    public JpaUpdateInboxRepositoryAdapter(
        JpaUpdateInboxRepository repository, EntityManager entityManager, SessionProperties properties
    ) {
        super(repository, entityManager, properties);
    }

    /**
     * Могут быть пустые результаты когда в БД данные есть, это потому что происходит гонка за строками и при
     * использовании for update если строка уже заблокирована, то возвращается пустое значение так как не получается
     * заблокировать строку.
     *
     * @return найденная сущность.
     */
    @Override
    protected Optional<JpaUpdateInbox> findWithMaxThreadsPerUser(int maxThreadsPerUser) {
        List<JpaUpdateInbox> inboxes = repository.findNextWithLimit(maxThreadsPerUser);
        return inboxes.stream()
            .filter(inbox -> inbox.getStatus() == UpdateInboxStatus.NEW)
            .min(Comparator.comparing(JpaUpdateInbox::getCreatedAt));
    }
}
