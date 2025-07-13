package io.github.drednote.telegram.datasource.session;

import io.github.drednote.telegram.datasource.DataSourceAdapter;
import java.util.List;
import java.util.Optional;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Contract for working with persisted {@link UpdateInbox} entities.
 * <p>
 * Defines operations for saving new updates from Telegram, updating existing inbox records, fetching the next available
 * update for processing, and timing out long-running tasks.
 * </p>
 *
 * @param <T> the inbox entity type
 * @author Ivan Galushko
 */
public interface UpdateInboxRepositoryAdapter<T extends UpdateInbox> extends DataSourceAdapter {

    /**
     * Saves a list of new updates from Telegram.
     *
     * @param updates raw Telegram updates to persist
     */
    void persist(List<Update> updates);

    /**
     * Updates the state of an existing {@link UpdateInbox} entity in the repository.
     *
     * @param updateInbox the inbox entity to update
     */
    void update(T updateInbox);

    /**
     * Retrieves the next available update to process from the inbox.
     * <p>
     * If no updates are available or none match the selection strategy, an empty {@link Optional} is returned.
     *
     * @return optional next inbox entity to process
     */
    Optional<T> findNextUpdate();

    /**
     * Marks long-running inbox entities with status {@link UpdateInboxStatus#IN_PROGRESS} as
     * {@link UpdateInboxStatus#TIMEOUT}, if they exceed the configured idle threshold.
     */
    void timeoutTasks();
}
