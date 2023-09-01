package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import java.util.List;
import org.springframework.lang.NonNull;

/**
 * Represents an interface for providing pre-update and post-update filters for Telegram update
 * requests.
 *
 * <p>Classes implementing this interface should define methods for obtaining lists of pre-update
 * and post-update filters based on the provided {@link UpdateRequest}
 *
 * @author Ivan Galushko
 * @implNote All implementations must support creating beans wth any scopes
 * @see PreUpdateFilter
 * @see PostUpdateFilter
 */
public interface UpdateFilterProvider {

  /**
   * Retrieves a list of pre-update filters to be applied to the incoming Telegram update request
   * before main processing by {@link UpdateHandler}
   *
   * @param request The incoming Telegram update request. Must not be null
   * @return A list of pre-update filters
   */
  List<PreUpdateFilter> getPreFilters(@NonNull UpdateRequest request);

  /**
   * Retrieves a list of post-update filters to be applied after processing the Telegram update
   * request with {@link UpdateHandler}
   *
   * @param request The incoming Telegram update request.Must not be null
   * @return A list of post-update filters
   */
  List<PostUpdateFilter> getPostFilters(@NonNull UpdateRequest request);
}
