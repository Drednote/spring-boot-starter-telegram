package io.github.drednote.telegram.datasource.session;

import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Base class representing a Telegram {@link Update} stored in an inbox queue for asynchronous processing.
 * <p>
 * Used for managing incoming updates in scenarios where processing must be decoupled from reception, such as
 * parallelism control, retries, or scheduled consumption.
 *
 * <p>Each {@code UpdateInbox} entity tracks:
 * <ul>
 *   <li>The Telegram update and its identifier</li>
 *   <li>The current {@link UpdateInboxStatus} (e.g. NEW, IN_PROGRESS, TIMEOUT)</li>
 *   <li>The {@code entityId}, which typically corresponds to the Telegram {@code userId}</li>
 *   <li>Error metadata to assist in diagnostics</li>
 * </ul>
 *
 * <p>
 * Implementations of this class are expected to be concrete entities (e.g. JPA).
 *
 * @author Ivan Galushko
 */
public abstract class UpdateInbox {

    /**
     * Returns the unique identifier of the Telegram update.
     *
     * @return Telegram update ID
     */
    public abstract Integer getUpdateId();

    /**
     * Returns the actual Telegram {@link Update} associated with this inbox entry.
     *
     * @return Telegram update
     */
    public abstract Update getUpdate();

    /**
     * Returns the logical identifier for the associated entity (typically the {@code userId}).
     * <p>
     * This is used to group updates by source, enabling strategies such as limiting
     * parallel processing per user or session.
     * </p>
     *
     * @return string ID representing the source entity of the update (e.g. user ID)
     */
    @Nullable
    public abstract String getEntityId();

    /**
     * Returns the current processing status of this inbox entry.
     *
     * @return status of the update (e.g., NEW, IN_PROGRESS, TIMEOUT)
     */
    public abstract UpdateInboxStatus getStatus();

    /**
     * Returns the error description associated with this update, if any.
     * <p>
     * Populated when processing fails.
     * </p>
     *
     * @return human-readable error message or stack trace
     */
    public abstract String getErrorDescription();

    /**
     * Sets the current processing status for this update inbox entry.
     *
     * @param status update processing status
     */
    public abstract void setStatus(UpdateInboxStatus status);

    /**
     * Sets a description of an error that occurred during processing.
     *
     * @param errorDescription human-readable explanation of failure
     */
    public abstract void setErrorDescription(String errorDescription);

    /**
     * Sets the unique identifier of the Telegram update.
     *
     * @param updateId Telegram update ID
     */
    public abstract void setUpdateId(Integer updateId);

    /**
     * Sets the raw {@link Update} object associated with this inbox entry.
     *
     * @param update Telegram update
     */
    public abstract void setUpdate(Update update);

    /**
     * Sets the logical identifier for the source entity of this update (e.g. user ID).
     * <p>
     * This field is used to associate updates with users or sessions for concurrency control.
     * </p>
     *
     * @param entityId user ID or similar identifier
     */
    public abstract void setEntityId(@Nullable String entityId);
}
