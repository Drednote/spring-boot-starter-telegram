package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.filter.post.PostFilterOrderComparator;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PreFilterOrderComparator;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import io.github.drednote.telegram.session.TelegramScope;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;

/**
 * Default implementation of the {@link UpdateFilterProvider} interface.
 *
 * <p>This class provides the implementation for retrieving pre-update and post-update filters
 * based on the provided {@link TelegramUpdateRequest}. It uses {@link ObjectProvider} to manage the
 * instances of pre-update and post-update filters, to support creating beans with Telegram scope or
 * any other
 *
 * @author Ivan Galushko
 * @see ObjectProvider
 * @see TelegramScope
 */
public class DefaultUpdateFilterProvider implements UpdateFilterProvider {

  private final ObjectProvider<PreUpdateFilter> preFilters;
  private final ObjectProvider<PostUpdateFilter> postFilters;

  /**
   * Constructs a {@code DefaultUpdateFilterProvider} with the specified pre-filter and post-filter
   * providers
   *
   * @param preFilters  The provider for pre-update filters. Must not be null
   * @param postFilters The provider for post-update filters. Must not be null
   */
  public DefaultUpdateFilterProvider(
      ObjectProvider<PreUpdateFilter> preFilters,
      ObjectProvider<PostUpdateFilter> postFilters
  ) {
    Assert.required(preFilters, "PreUpdateFilters provider");
    Assert.required(postFilters, "PostUpdateFilters provider");

    this.preFilters = preFilters;
    this.postFilters = postFilters;
  }

  /**
   * Retrieves a list of pre-update filters to be applied to the incoming Telegram update request
   *
   * @param request The incoming Telegram update request. Must not be null
   * @return A list of pre-update filters
   * @implNote Any bean scopes are supported
   */
  @Override
  public List<PreUpdateFilter> getPreFilters(TelegramUpdateRequest request) {
    Assert.notNull(request, "TelegramUpdateRequest");
    return new ArrayList<>(preFilters.stream()
        .filter(updateFilter -> updateFilter.matches(request))
        .sorted(PreFilterOrderComparator.INSTANCE)
        .toList());
  }

  /**
   * Retrieves a list of post-update filters to be applied after processing the Telegram update
   * request
   *
   * @param request The incoming Telegram update request. Must not be null
   * @return A list of post-update filters
   * @implNote Any bean scopes are supported
   */
  @Override
  public List<PostUpdateFilter> getPostFilters(TelegramUpdateRequest request) {
    Assert.notNull(request, "TelegramUpdateRequest");
    return new ArrayList<>(postFilters.stream()
        .filter(updateFilter -> updateFilter.matches(request))
        .sorted(PostFilterOrderComparator.INSTANCE)
        .toList());
  }
}
