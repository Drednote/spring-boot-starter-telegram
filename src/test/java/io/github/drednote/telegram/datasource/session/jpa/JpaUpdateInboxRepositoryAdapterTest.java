package io.github.drednote.telegram.datasource.session.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import io.github.drednote.telegram.datasource.session.jpa.JpaUpdateInboxRepositoryAdapterTest.Config;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.support.jpa.PostgresSqlTest;
import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Import(Config.class)
@Sql(statements = {"""
     insert into update_inbox(update_id, entity_id, update, status, error_description, created_at, updated_at) VALUES\s
       (1, '1', '{}', 'NEW', null, '2025-06-02 22:07:05.000000', '2025-06-02 22:08:05.000000'),
       (2, '1', '{}', 'NEW', null, '2025-06-02 22:08:05.000000', '2025-06-02 22:08:05.000000');
    \s"""
}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
class JpaUpdateInboxRepositoryAdapterTest extends PostgresSqlTest {

    @Autowired
    @Qualifier("jpaUpdateInboxRepositoryAdapterCountDownLatch")
    private CountDownLatch firstLatch;
    @Autowired
    @Qualifier("testUpdateInboxRepositoryAdapterCountDownLatch")
    private CountDownLatch secondLatch;
    @Qualifier("jpaUpdateInboxRepositoryAdapter2")
    @Autowired
    private JpaUpdateInboxRepositoryAdapter jpaAdapter;
    @Autowired
    private TestUpdateInboxRepositoryAdapter adapter;
    @Autowired
    private JpaUpdateInboxRepository jpaUpdateInboxRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldBlockRowForRead() {
        assertThat(jpaUpdateInboxRepository.findAll()).hasSize(2);

        Set<String> entityIds = new HashSet<>();
        CompletableFuture<Void> first = CompletableFuture.runAsync(() -> {
            Optional<JpaUpdateInbox> nextUpdate = adapter.findNextUpdate();
            assertThat(nextUpdate).isPresent();
            nextUpdate.ifPresent(jpaUpdateInbox -> {
                if (!entityIds.add(jpaUpdateInbox.getEntityId())) {
                    fail("duplicate entity id: " + jpaUpdateInbox.getEntityId());
                }
            });
        });
        CompletableFuture<Void> second = CompletableFuture.runAsync(() -> {
            try {
                boolean await = secondLatch.await(5, TimeUnit.SECONDS);
                if (!await) {
                    throw new RuntimeException("Timed out waiting for update");
                }
                Optional<JpaUpdateInbox> nextUpdate = jpaAdapter.findNextUpdate();
                assertThat(nextUpdate).isEmpty();
                firstLatch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        CompletableFuture.allOf(first, second).join();
        assertThat(entityIds).hasSize(1);

        entityManager.flush();
        entityManager.clear();

        assertThat(jpaUpdateInboxRepository.findById(1).get().getStatus()).isEqualTo(UpdateInboxStatus.IN_PROGRESS);
        assertThat(jpaUpdateInboxRepository.findById(2).get().getStatus()).isEqualTo(UpdateInboxStatus.NEW);
    }

    static class TestUpdateInboxRepositoryAdapter extends JpaUpdateInboxRepositoryAdapter {

        private final CountDownLatch waitLatch;
        private final CountDownLatch executeLatch;

        public TestUpdateInboxRepositoryAdapter(
            CountDownLatch waitLatch, CountDownLatch executeLatch,
            JpaUpdateInboxRepository repository, EntityManager entityManager, SessionProperties sessionProperties
        ) {
            super(repository, entityManager, sessionProperties);
            this.waitLatch = waitLatch;
            this.executeLatch = executeLatch;
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public Optional<JpaUpdateInbox> findNextUpdate() {
            Optional<JpaUpdateInbox> nextUpdate = super.findNextUpdate();
            try {
                executeLatch.countDown();
                boolean await = waitLatch.await(5, TimeUnit.SECONDS);
                if (!await) {
                    throw new RuntimeException("Timed out waiting for update");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return nextUpdate;
        }
    }

    @TestConfiguration
    static class Config {

        @Bean
        public JpaUpdateInboxRepositoryAdapter jpaUpdateInboxRepositoryAdapter2(
            JpaUpdateInboxRepository repository, EntityManager entityManager, SessionProperties sessionProperties
        ) {
            return new JpaUpdateInboxRepositoryAdapter(repository, entityManager, sessionProperties);
        }

        @Bean
        public TestUpdateInboxRepositoryAdapter testUpdateInboxRepositoryAdapter(
            JpaUpdateInboxRepository repository, EntityManager entityManager, SessionProperties sessionProperties,
            @Qualifier("jpaUpdateInboxRepositoryAdapterCountDownLatch") CountDownLatch waitLatch,
            @Qualifier("testUpdateInboxRepositoryAdapterCountDownLatch") CountDownLatch executeLatch
        ) {
            return new TestUpdateInboxRepositoryAdapter(waitLatch, executeLatch, repository, entityManager,
                sessionProperties);
        }

        @Bean
        public SessionProperties sessionProperties() {
            SessionProperties properties = new SessionProperties();
            properties.setMaxThreadsPerUser(1);
            return properties;
        }

        @Bean
        public CountDownLatch jpaUpdateInboxRepositoryAdapterCountDownLatch() {
          return new CountDownLatch(1);
        }


        @Bean
        public CountDownLatch testUpdateInboxRepositoryAdapterCountDownLatch() {
            return new CountDownLatch(1);
        }
    }
}