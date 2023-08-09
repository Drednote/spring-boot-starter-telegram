package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;

public class DefaultUpdateFilterProvider implements UpdateFilterProvider {

  private final ObjectProvider<UpdateFilter> filters;

  public DefaultUpdateFilterProvider(ObjectProvider<UpdateFilter> filters) {
    this.filters = filters;
  }

  @Override
  public List<UpdateFilter> getPreFilters(TelegramUpdateRequest request) {
    return new ArrayList<>(filters.stream()
        .filter(updateFilter -> updateFilter.matches(request))
        .sorted(FilterOrderComparator.PRE_INSTANCE)
        .toList());
  }

  @Override
  public List<UpdateFilter> getPostFilters(TelegramUpdateRequest request) {
    return new ArrayList<>(filters.stream()
        .filter(updateFilter -> updateFilter.matches(request))
        .sorted(FilterOrderComparator.POST_INSTANCE)
        .toList());
  }
}
