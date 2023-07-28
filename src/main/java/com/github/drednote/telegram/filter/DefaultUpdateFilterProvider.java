package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.datasource.DataSourceAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.OrderComparator;

public class DefaultUpdateFilterProvider implements UpdateFilterProvider {

  private final List<UpdateFilter> filters;

  public DefaultUpdateFilterProvider(
      ObjectProvider<DataSourceAdapter> dataSourceAdapter,
      PermissionProperties permissionProperties,
      Collection<UpdateFilter> filters
  ) {
    this.filters = new ArrayList<>(filters);

    this.filters.add(new RoleUpdateFilter(dataSourceAdapter, permissionProperties));
    this.filters.add(new AccessPermissionUpdateFilter(permissionProperties));

    this.filters.sort(OrderComparator.INSTANCE);
  }

  @Override
  public Collection<UpdateFilter> resolve(UpdateRequest request) {
    return new ArrayList<>(filters);
  }
}
