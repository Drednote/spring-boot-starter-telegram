package io.github.drednote.telegram.datasource.session;

import io.github.drednote.telegram.datasource.DataSourceAdapter;
import java.util.List;
import java.util.Optional;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Ivan Galushko
 */
public interface UpdateInboxRepositoryAdapter<T extends UpdateInbox> extends DataSourceAdapter {

    /**
     * Save new update.
     *
     * @param updates update from telegram
     */
    void persist(List<Update> updates);

    /**
     * Update the exsited updateInbox.
     *
     * @param updateInbox update
     */
    void update(T updateInbox);

    /**
     * Search for the next update to process. If there are no updates that match the selection, returns empty.
     *
     * @return updateInbox
     */
    Optional<T> findNextUpdate();

    /**
     * Updates "hung" updates by setting their status to {@link UpdateInboxStatus#TIMEOUT}.
     */
    void timeoutTasks();
}
