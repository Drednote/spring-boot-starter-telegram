package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;

public class DefaultUpdateFilterProvider implements UpdateFilterProvider {

  private final ObjectProvider<PreUpdateFilter> preFilters;
  private final ObjectProvider<PostUpdateFilter> postFilters;

  public DefaultUpdateFilterProvider(
      ObjectProvider<PreUpdateFilter> prefilters,
      ObjectProvider<PostUpdateFilter> postFilters
  ) {
    this.preFilters = prefilters;
    this.postFilters = postFilters;
  }

  @Override
  public List<PreUpdateFilter> getPreFilters(TelegramUpdateRequest request) {
    return new ArrayList<>(preFilters.stream()
        .filter(updateFilter -> updateFilter.matches(request))
        .sorted(PreFilterOrderComparator.INSTANCE)
        .toList());
  }

  @Override
  public List<PostUpdateFilter> getPostFilters(TelegramUpdateRequest request) {
    return new ArrayList<>(postFilters.stream()
        .filter(updateFilter -> updateFilter.matches(request))
        .sorted(PostFilterOrderComparator.INSTANCE)
        .toList());
  }
}
