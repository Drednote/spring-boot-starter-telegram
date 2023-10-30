package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.datasource.permission.PermissionRepositoryAdapter;
import io.github.drednote.telegram.filter.post.NotHandledUpdateFilter;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.AccessPermissionFilter;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import io.github.drednote.telegram.filter.pre.RoleFilter;
import io.github.drednote.telegram.filter.pre.UserRateLimitRequestFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Autoconfiguration class for setting up update filters and related properties.
 *
 * @author Ivan Galushko
 */
@AutoConfiguration
@EnableConfigurationProperties({FilterProperties.class, PermissionProperties.class})
public class FiltersAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public UpdateFilterProvider updateFilterProvider(
      ObjectProvider<PreUpdateFilter> prefilters,
      ObjectProvider<PostUpdateFilter> postFilters
  ) {
    return new DefaultUpdateFilterProvider(prefilters, postFilters);
  }

  @Bean
  @ConditionalOnMissingBean
  public RoleFilter roleFilter(
      PermissionRepositoryAdapter permissionRepositoryAdapter, PermissionProperties permissionProperties
  ) {
    return new RoleFilter(permissionRepositoryAdapter, permissionProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public AccessPermissionFilter accessPermissionFilter(PermissionProperties permissionProperties) {
    return new AccessPermissionFilter(permissionProperties);
  }

  @Bean
  @ConditionalOnMissingBean
  public UserRateLimitRequestFilter concurrentUserRequestFilter(FilterProperties properties) {
    return new UserRateLimitRequestFilter(properties);
  }

  @Bean
  @ConditionalOnMissingBean
  public NotHandledUpdateFilter notHandledUpdateFilter() {
    return new NotHandledUpdateFilter();
  }
}
