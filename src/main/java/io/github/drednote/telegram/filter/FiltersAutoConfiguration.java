package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.datasource.permission.PermissionRepositoryAdapter;
import io.github.drednote.telegram.filter.post.ConclusivePostUpdateFilter;
import io.github.drednote.telegram.filter.post.NotHandledUpdateFilter;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.post.ScenarioIdPersistFilter;
import io.github.drednote.telegram.filter.pre.AccessPermissionFilter;
import io.github.drednote.telegram.filter.pre.HasRoleRequestFilter;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import io.github.drednote.telegram.filter.pre.RoleFilter;
import io.github.drednote.telegram.filter.pre.UserRateLimitRequestFilter;
import io.github.drednote.telegram.handler.UpdateHandlerAutoConfiguration;
import io.github.drednote.telegram.utils.FieldProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

/**
 * Autoconfiguration class for setting up update filters and related properties.
 *
 * @author Ivan Galushko
 */
@AutoConfiguration
@EnableConfigurationProperties({FilterProperties.class, PermissionProperties.class})
@AutoConfigureAfter(UpdateHandlerAutoConfiguration.class)
public class FiltersAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UpdateFilterProvider updateFilterProvider(
        ObjectProvider<PreUpdateFilter> prefilters,
        ObjectProvider<PostUpdateFilter> postFilters,
        ObjectProvider<ConclusivePostUpdateFilter> conclusivePostUpdateFilters
    ) {
        return new DefaultUpdateFilterProvider(prefilters, postFilters, conclusivePostUpdateFilters);
    }

    @Bean
    @ConditionalOnMissingBean
    public RoleFilter roleFilter(
        @Autowired(required = false) @Nullable PermissionRepositoryAdapter permissionRepositoryAdapter,
        PermissionProperties permissionProperties
    ) {
        return new RoleFilter(FieldProvider.create(permissionRepositoryAdapter), permissionProperties);
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

    @Bean
    @ConditionalOnMissingBean
    public HasRoleRequestFilter hasRoleRequestFilter() {
        return new HasRoleRequestFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScenarioIdPersistFilter scenarioIdUpdater() {
      return new ScenarioIdPersistFilter();
    }
}
