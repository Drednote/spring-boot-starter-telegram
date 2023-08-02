package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.request.BotRequest;
import com.github.drednote.telegram.utils.FilterOrderComparator;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;

public class DefaultUpdateFilterProvider implements UpdateFilterProvider {

  private final ObjectProvider<UpdateFilter> filters;

  public DefaultUpdateFilterProvider(ObjectProvider<UpdateFilter> filters) {
    this.filters = filters;
  }

  @Override
  public List<UpdateFilter> getPreFilters(BotRequest request) {
    return new ArrayList<>(filters.stream().sorted(FilterOrderComparator.PRE_INSTANCE).toList());
  }

  @Override
  public List<UpdateFilter> getPostFilters(BotRequest request) {
    return new ArrayList<>(filters.stream().sorted(FilterOrderComparator.POST_INSTANCE).toList());
  }
}
