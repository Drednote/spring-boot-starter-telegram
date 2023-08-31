package io.github.drednote.telegram.datasource;

import io.github.drednote.telegram.core.annotation.BetaApi;
import org.springframework.data.repository.CrudRepository;

@BetaApi
public record DataSourceAdapterImpl(
    CrudRepository<? extends Permission, Long> permissionRepository,
    CrudRepository<? extends ScenarioDB, Long> scenarioRepository
) implements DataSourceAdapter {}
