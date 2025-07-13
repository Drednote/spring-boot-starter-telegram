package io.github.drednote.telegram.datasource.session;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Represents the processing status of a Telegram {@link Update} within the inbox queue.
 * <p>
 * Used by {@link UpdateInbox} to track the lifecycle of each update during asynchronous processing.
 *
 * @author Ivan Galushko
 */
public enum UpdateInboxStatus {
    /**
     * Freshly received update, not yet picked up for processing.
     */
    NEW,
    /**
     * Update is currently processing.
     */
    IN_PROGRESS,
    /**
     * Update was successfully handled and can be removed or archived.
     */
    PROCESSED,
    /**
     * An error occurred during processing; may require retry or manual inspection.
     */
    ERROR,
    /**
     * Update was stuck in {@code IN_PROGRESS} for too long and marked as idle.
     */
    TIMEOUT
}
