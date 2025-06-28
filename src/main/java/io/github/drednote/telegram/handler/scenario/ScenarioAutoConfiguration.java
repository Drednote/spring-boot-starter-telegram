package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.datasource.scenario.InMemoryScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioRepository;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.InMemoryScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.jpa.JpaScenarioIdRepository;
import io.github.drednote.telegram.datasource.scenarioid.jpa.JpaScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.filter.pre.ScenarioUpdateHandlerPopular;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder.ScenarioData;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.SimpleScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.SimpleScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.machine.MachineScenarioPersister;
import io.github.drednote.telegram.handler.scenario.persist.MachineScenarioFactory;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioFactory;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryBeanPostProcessor;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryContainer;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryResolver;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties;
import io.github.drednote.telegram.handler.scenario.property.ScenarioPropertiesConfigurer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
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
    public static class DatabaseScenarioAutoConfiguration {

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

    @AutoConfiguration
    public static class InMemoryScenarioAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean({ScenarioIdRepositoryAdapter.class, JpaScenarioIdRepository.class})
        public ScenarioIdRepositoryAdapter inMemoryScenarioIdRepositoryAdapter() {
            return new InMemoryScenarioIdRepositoryAdapter();
        }

        @Bean
        @ConditionalOnMissingBean({ScenarioRepositoryAdapter.class, JpaScenarioRepository.class})
        public <S> ScenarioRepositoryAdapter<S> inMemoryScenarioRepositoryAdapter() {
            return new InMemoryScenarioRepositoryAdapter<>();
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
        ScenarioRepositoryAdapter<S> scenarioRepositoryAdapter,
        ScenarioFactoryResolver scenarioFactoryResolver, ScenarioProperties scenarioProperties
    ) throws Exception {
        ScenarioBuilder<S> builder = new ScenarioBuilder<>(StateMachineBuilder.builder());

        adapter.onConfigure(new SimpleScenarioConfigConfigurer<>(builder));
        SimpleScenarioStateConfigurer<S> stateConfigurer = new SimpleScenarioStateConfigurer<>(builder);
        adapter.onConfigure(stateConfigurer);
        adapter.onConfigure(new SimpleScenarioTransitionConfigurer<>(builder));
        new ScenarioPropertiesConfigurer<>(builder, scenarioProperties, scenarioFactoryResolver).configure(stateConfigurer.withStates());
        ScenarioData<S> data = builder.build();

        ScenarioIdResolver resolver = data.resolver() == null
            ? new SimpleScenarioIdResolver(scenarioIdAdapter)
            : data.resolver();

        ScenarioPersister<S> persist;
        if (data.persister() == null) {
            persist = data.adapter() == null
                ? new MachineScenarioPersister<>(scenarioRepositoryAdapter)
                : new MachineScenarioPersister<>(data.adapter());
        } else {
            persist = data.persister();
        }

        ScenarioFactory<S> factory = new MachineScenarioFactory<>(data.factory(), persist, resolver);

        return new ScenarioUpdateHandlerPopular<>(persist, factory, resolver);
    }
}