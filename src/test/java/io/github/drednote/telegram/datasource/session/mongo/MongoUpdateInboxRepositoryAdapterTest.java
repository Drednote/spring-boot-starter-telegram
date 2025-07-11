package io.github.drednote.telegram.datasource.session.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.fail;

import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import io.github.drednote.telegram.datasource.session.mongo.MongoUpdateInboxRepositoryAdapterTest.Config;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.support.builder.UpdateBuilder;
import io.github.drednote.telegram.support.mongo.MongoTest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Import(Config.class)
class MongoUpdateInboxRepositoryAdapterTest extends MongoTest {

    @Autowired
    @Qualifier("jpaUpdateInboxRepositoryAdapterCountDownLatch")
    private CountDownLatch firstLatch;
    @Autowired
    @Qualifier("testUpdateInboxRepositoryAdapterCountDownLatch")
    private CountDownLatch secondLatch;
    @Qualifier("mongoUpdateInboxRepositoryAdapter2")
    @Autowired
    private MongoUpdateInboxRepositoryAdapter adapter;
    @Autowired
    private TestUpdateInboxRepositoryAdapter testAdapter;
    @Autowired
    private MongoUpdateInboxRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldBlockRowForRead() {
        assertThatNoException().isThrownBy(() -> adapter.persist(List.of(
            UpdateBuilder._default("1").withUpdateId(1).message(),
            UpdateBuilder._default("2").withUpdateId(2).message()
        )));

        assertThat(repository.findAll()).hasSize(2);

        Set<String> entityIds = new HashSet<>();
        CompletableFuture<Void> first = CompletableFuture.runAsync(() -> {
            Optional<MongoUpdateInbox> nextUpdate = testAdapter.findNextUpdate();
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
                Optional<MongoUpdateInbox> nextUpdate = adapter.findNextUpdate();
                assertThat(nextUpdate).isEmpty();
                firstLatch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        CompletableFuture.allOf(first, second).join();
        assertThat(entityIds).hasSize(1);

        assertThat(repository.findById(1).get().getStatus()).isEqualTo(UpdateInboxStatus.IN_PROGRESS);
        assertThat(repository.findById(2).get().getStatus()).isEqualTo(UpdateInboxStatus.NEW);
    }

    @Test
    void shouldTakeOnlyTwoTasks() {
        assertThatNoException().isThrownBy(() -> adapter.persist(List.of(
            UpdateBuilder._default("1").withUpdateId(1).withUser(1L).message(),
            UpdateBuilder._default("2").withUpdateId(2).withUser(1L).message(),
            UpdateBuilder._default("3").withUpdateId(3).withUser(2L).message(),
            UpdateBuilder._default("4").withUpdateId(4).withUser(2L).message()
        )));

        CompletableFuture<Void>[] futures = new CompletableFuture[10];
        for (int i = 0; i < 10; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                adapter.findNextUpdate();
            });
            futures[i] = future;
        }

        CompletableFuture.allOf(futures).join();

        assertThat(repository.findById(1).get().getStatus()).isEqualTo(UpdateInboxStatus.IN_PROGRESS);
        assertThat(repository.findById(2).get().getStatus()).isEqualTo(UpdateInboxStatus.NEW);
        assertThat(repository.findById(3).get().getStatus()).isEqualTo(UpdateInboxStatus.IN_PROGRESS);
        assertThat(repository.findById(4).get().getStatus()).isEqualTo(UpdateInboxStatus.NEW);
    }

    @Test
    void shouldCorrectGetInWorkIfEntityIdIsNull() {
        assertThatNoException().isThrownBy(() -> adapter.persist(List.of(
            UpdateBuilder._default("1").withUpdateId(1).withUser(1L).message(),
            UpdateBuilder._default("4").withUpdateId(4).withUser(1L).poll()
        )));

        adapter.findNextUpdate();
        adapter.findNextUpdate();

        assertThat(repository.findById(1).get().getStatus()).isEqualTo(UpdateInboxStatus.IN_PROGRESS);
        assertThat(repository.findById(4).get().getStatus()).isEqualTo(UpdateInboxStatus.IN_PROGRESS);
    }

    @Test
    void shouldCorrectGetWithoutLimit() {
        assertThatNoException().isThrownBy(() -> adapter.persist(List.of(
            UpdateBuilder._default("1").withUpdateId(1).withUser(1L).message(),
            UpdateBuilder._default("4").withUpdateId(4).withUser(1L).message()
        )));

        adapter.findNextWithoutLimit();
        adapter.findNextWithoutLimit();

        assertThat(repository.findById(1).get().getStatus()).isEqualTo(UpdateInboxStatus.IN_PROGRESS);
        assertThat(repository.findById(4).get().getStatus()).isEqualTo(UpdateInboxStatus.IN_PROGRESS);
    }

    static class TestUpdateInboxRepositoryAdapter extends MongoUpdateInboxRepositoryAdapter {

        private final CountDownLatch waitLatch;
        private final CountDownLatch executeLatch;

        public TestUpdateInboxRepositoryAdapter(
            CountDownLatch waitLatch, CountDownLatch executeLatch,
            MongoUpdateInboxRepository repository, SessionProperties sessionProperties, MongoTemplate mongoTemplate
        ) {
            super(sessionProperties, repository, mongoTemplate);
            this.waitLatch = waitLatch;
            this.executeLatch = executeLatch;
        }

        @Override
        public Optional<MongoUpdateInbox> findNextUpdate() {
            Optional<MongoUpdateInbox> nextUpdate = super.findNextUpdate();
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
        public MongoUpdateInboxRepositoryAdapter mongoUpdateInboxRepositoryAdapter2(
            MongoUpdateInboxRepository repository, SessionProperties sessionProperties, MongoTemplate mongoTemplate
        ) {
            return new MongoUpdateInboxRepositoryAdapter(sessionProperties, repository, mongoTemplate);
        }

        @Bean
        public TestUpdateInboxRepositoryAdapter testUpdateInboxRepositoryAdapter(
            MongoUpdateInboxRepository repository, SessionProperties sessionProperties, MongoTemplate mongoTemplate,
            @Qualifier("jpaUpdateInboxRepositoryAdapterCountDownLatch") CountDownLatch waitLatch,
            @Qualifier("testUpdateInboxRepositoryAdapterCountDownLatch") CountDownLatch executeLatch
        ) {
            return new TestUpdateInboxRepositoryAdapter(waitLatch, executeLatch, repository, sessionProperties,
                mongoTemplate);
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

        @Bean
        public MongoCustomConversions mongoCustomConversions() {
            List<Converter<?, ?>> converters = new ArrayList<>();
            converters.add(new DocumentToUpdateConverter(null));
            converters.add(new UpdateToDocumentConverter(null));
            return new MongoCustomConversions(converters);
        }
    }
}