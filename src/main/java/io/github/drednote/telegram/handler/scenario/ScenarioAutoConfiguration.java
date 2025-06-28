package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioRepository;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.InMemoryScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepository;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.jpa.JpaScenarioIdRepository;
import io.github.drednote.telegram.datasource.scenarioid.jpa.JpaScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.filter.pre.ScenarioUpdateHandlerPopular;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder.ScenarioData;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.DefaultScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.DefaultScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.DefaultScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.factory.DefaultScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.factory.MachineScenarioFactory;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioFactory;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.persist.InMemoryScenarioPersister;
import io.github.drednote.telegram.handler.scenario.persist.DefaultScenarioPersister;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryBeanPostProcessor;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryContainer;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryResolver;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties;
import io.github.drednote.telegram.handler.scenario.property.ScenarioPropertiesConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.config.StateMachineBuilder;

@AutoConfiguration
@ConditionalOnProperty(
    prefix = "drednote.telegram.update-handler",
    name = "scenario-enabled",
    havingValue = "true",
    matchIfMissing = true
)
@ConditionalOnBean(ScenarioConfigurerAdapter.class)
public class ScenarioAutoConfiguration {

    @AutoConfiguration
    public static class DatasourceScenarioAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean({ScenarioIdRepositoryAdapter.class, ScenarioIdRepository.class})
        public ScenarioIdRepositoryAdapter inMemoryScenarioIdRepositoryAdapter() {
            return new InMemoryScenarioIdRepositoryAdapter();
        }

        @AutoConfiguration
        @ConditionalOnClass(JpaRepository.class)
        public static class JpaScenarioAutoConfiguration {

            @Bean
            @ConditionalOnMissingBean(ScenarioIdRepositoryAdapter.class)
            @ConditionalOnBean(JpaScenarioIdRepository.class)
            public ScenarioIdRepositoryAdapter scenarioIdRepositoryAdapter(
                JpaScenarioIdRepository scenarioIdRepository
            ) {
                return new JpaScenarioIdRepositoryAdapter(scenarioIdRepository);
            }

            @Bean
            @ConditionalOnMissingBean(ScenarioRepositoryAdapter.class)
            @ConditionalOnBean(JpaScenarioRepository.class)
            public <S> ScenarioRepositoryAdapter<S> scenarioRepositoryAdapter(JpaScenarioRepository repository) {
                return new JpaScenarioRepositoryAdapter<>(repository);
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public ScenarioUpdateHandler scenarioUpdateHandler() {
        return new ScenarioUpdateHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScenarioFactoryBeanPostProcessor scenarioFactoryBeanPostProcessor(ScenarioFactoryContainer container) {
        return new ScenarioFactoryBeanPostProcessor(container);
    }

    @Bean
    @ConditionalOnMissingBean
    public ScenarioFactoryContainer scenarioFactoryContainer() {
        return new ScenarioFactoryContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    public <S> ScenarioUpdateHandlerPopular<S> scenarioUpdateHandlerPopular(
        ScenarioConfigurerAdapter<S> adapter, ScenarioIdRepositoryAdapter scenarioIdAdapter,
        @Autowired(required = false) @Nullable ScenarioRepositoryAdapter<S> scenarioRepositoryAdapter,
        ScenarioFactoryResolver scenarioFactoryResolver, ScenarioProperties scenarioProperties
    ) throws Exception {
        ScenarioBuilder<S> builder = new ScenarioBuilder<>(StateMachineBuilder.builder());

        adapter.onConfigure(new DefaultScenarioConfigConfigurer<>(builder));
        DefaultScenarioStateConfigurer<S> stateConfigurer = new DefaultScenarioStateConfigurer<>(builder);
        adapter.onConfigure(stateConfigurer);
        adapter.onConfigure(new DefaultScenarioTransitionConfigurer<>(builder));
        new ScenarioPropertiesConfigurer<>(builder, scenarioProperties, scenarioFactoryResolver)
            .configure(stateConfigurer.withStates());
        ScenarioData<S> data = builder.build();

        ScenarioIdResolver resolver = data.resolver() == null
            ? new DefaultScenarioIdResolver(scenarioIdAdapter)
            : data.resolver();

        ScenarioPersister<S> persist;
        if (data.persister() == null) {
            persist = scenarioRepositoryAdapter == null
                ? new InMemoryScenarioPersister<>()
                : new DefaultScenarioPersister<>(scenarioRepositoryAdapter);
        } else {
            persist = data.persister();
        }

        ScenarioFactory<S> factory = new MachineScenarioFactory<>(data.factory(), persist, resolver);

        return new ScenarioUpdateHandlerPopular<>(persist, factory, resolver);
    }
}