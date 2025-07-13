package io.github.drednote.telegram.datasource.permission;

import io.github.drednote.telegram.datasource.DataSourceAdapter;
import org.springframework.lang.Nullable;

/**
 * Adapter interface for retrieving permission information from an external data source.
 * <p>
 * Used to fetch {@link Permission} objects for a given {@code chatId}, typically during request processing to determine
 * user access rights.
 * </p>
 *
 * @author Ivan Galushko
 */
public interface PermissionRepositoryAdapter extends DataSourceAdapter {

    /**
     * Retrieves the {@link Permission} associated with the given chat ID.
     *
     * @param chatId the Telegram chat ID of the user
     * @return the corresponding {@link Permission}, or {@code null} if none found
     */
    @Nullable
    Permission findPermission(Long chatId);
}
