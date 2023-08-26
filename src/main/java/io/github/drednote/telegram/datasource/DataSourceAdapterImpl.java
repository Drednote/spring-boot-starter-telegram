package io.github.drednote.telegram.datasource;

import org.springframework.data.repository.CrudRepository;

public record DataSourceAdapterImpl(
    CrudRepository<? extends Permission, Long> permissionRepository,
    CrudRepository<? extends ScenarioDB, Long> scenarioRepository
) implements DataSourceAdapter {}
