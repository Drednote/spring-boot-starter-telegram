package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.datasource.DataSourceAdapter;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = "io.github.drednote.examples.scenario")
@EntityScan(basePackageClasses = {DataSourceAdapter.class}, basePackages = "io.github.drednote.examples.scenario")
public class ScenarioConfig extends ScenarioConfigurerAdapter<State> {

    private final ScenarioRepository scenarioRepository;

    @Override
    public void onConfigure(@NonNull ScenarioTransitionConfigurer<State> configurer) {
        // empty because all configuration will be in properties
    }

    @Override
    public void onConfigure(ScenarioConfigConfigurer<State> configurer) {
        configurer
            .withPersister(new JpaScenarioRepositoryAdapter<>(scenarioRepository));
    }

    @Override
    public void onConfigure(ScenarioStateConfigurer<State> configurer) {
        configurer.withInitialState(State.INITIAL);
    }
}
