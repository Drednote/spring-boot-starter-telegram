package io.github.drednote.telegram.datasource.session.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.datasource.session.UpdateInboxStatus;
import io.github.drednote.telegram.session.SessionProperties;
import io.github.drednote.telegram.support.builder.UpdateBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

class InMemoryUpdateInboxRepositoryAdapterTest {

    @Test
    void shouldCorrectFindEntities() {
        SessionProperties properties = new SessionProperties();
        InMemoryUpdateInboxRepositoryAdapter adapter = new InMemoryUpdateInboxRepositoryAdapter(properties,
            new TelegramMessageSource());

        Update update = UpdateBuilder.create(1).withUser(1L).message();
        Update update2 = UpdateBuilder.create(2).withUser(1L).message();
        Update update3 = UpdateBuilder.create(3).withUser(2L).message();

        adapter.persist(List.of(update, update2, update3));

        Optional<InMemoryUpdateInbox> nextUpdate = adapter.findNextUpdate();
        assertThat(nextUpdate).isPresent();
        InMemoryUpdateInbox updateInbox = nextUpdate.get();
        assertThat(updateInbox.getUpdateId()).isEqualTo(1);
        assertThat(updateInbox.getStatus()).isEqualTo(UpdateInboxStatus.IN_PROGRESS);

        Optional<InMemoryUpdateInbox> nextUpdate2 = adapter.findNextUpdate();
        assertThat(nextUpdate2).isPresent();
        assertThat(nextUpdate2.get().getUpdateId()).isEqualTo(3);

        assertThat(adapter.findNextUpdate()).isEmpty();

        updateInbox.setStatus(UpdateInboxStatus.PROCESSED);
        adapter.update(updateInbox);

        Optional<InMemoryUpdateInbox> nextUpdate3 = adapter.findNextUpdate();
        assertThat(nextUpdate3).isPresent();
        assertThat(nextUpdate3.get().getUpdateId()).isEqualTo(2);
    }

    @Test
    void shouldCorrectWaitForEmpty() throws InterruptedException {
        SessionProperties properties = new SessionProperties();
        properties.setMaxMessagesInQueue(1);
        InMemoryUpdateInboxRepositoryAdapter adapter = new InMemoryUpdateInboxRepositoryAdapter(properties,
            new TelegramMessageSource());

        AtomicBoolean finished = new AtomicBoolean(false);
        adapter.persist(List.of(UpdateBuilder.create(1).message()));
        new Thread(() -> {
            adapter.persist(List.of(UpdateBuilder.create(1).message()));
            finished.set(true);
        }).start();

        Thread.sleep(100);

        assertThat(finished).isFalse();
    }
}