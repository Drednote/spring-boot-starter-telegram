package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.datasource.DataSourceAdapter;
import java.util.Collection;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({FilterProperties.class, PermissionProperties.class})
public class FiltersAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public UpdateFilterProvider updateFilterProvider(
      ObjectProvider<DataSourceAdapter> adapter, PermissionProperties permissionProperties,
      Collection<UpdateFilter> filters
  ) {
    return new DefaultUpdateFilterProvider(adapter, permissionProperties, filters);
  }
}
