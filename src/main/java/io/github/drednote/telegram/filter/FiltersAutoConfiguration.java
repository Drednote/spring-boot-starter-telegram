package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.datasource.DataSourceAdapter;
import io.github.drednote.telegram.session.SessionProperties;
import org.springframework.beans.factory.ObjectProvider;
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
      ObjectProvider<UpdateFilter> filters
  ) {
    return new DefaultUpdateFilterProvider(filters);
  }

  @Bean
  @ConditionalOnMissingBean
  public RoleFilter roleFilter(
      ObjectProvider<DataSourceAdapter> adapterProvider, PermissionProperties permissionProperties
  ) {
    return new RoleFilter(adapterProvider, permissionProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public AccessPermissionFilter accessPermissionFilter(PermissionProperties permissionProperties) {
    return new AccessPermissionFilter(permissionProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public ConcurrentUserRequestFilter concurrentUserRequestFilter(SessionProperties properties) {
    return new ConcurrentUserRequestFilter(properties);
  }
}