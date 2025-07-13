package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioRepository;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenario.mongo.MongoScenarioRepository;
import io.github.drednote.telegram.datasource.scenario.mongo.MongoScenarioRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.InMemoryScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.ScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.jpa.JpaScenarioIdRepository;
import io.github.drednote.telegram.datasource.scenarioid.jpa.JpaScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.datasource.scenarioid.mongo.MongoScenarioIdRepository;
import io.github.drednote.telegram.datasource.scenarioid.mongo.MongoScenarioIdRepositoryAdapter;
import io.github.drednote.telegram.filter.pre.ScenarioUpdateHandlerPopular;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder.ScenarioData;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.config.DefaultScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.state.DefaultScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.DefaultScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.factory.DefaultScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.factory.MachineScenarioFactory;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioFactory;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.persist.DefaultScenarioPersister;
import io.github.drednote.telegram.handler.scenario.persist.InMemoryScenarioPersister;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryBeanPostProcessor;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryContainer;
import io.github.drednote.telegram.handler.scenario.property.ScenarioFactoryResolver;
import io.github.drednote.telegram.handler.scenario.property.ScenarioProperties;
import io.github.drednote.telegram.handler.scenario.property.ScenarioPropertiesConfigurer;
import io.github.drednote.telegram.handler.scenario.spy.ScenarioStateMachineBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.Nullable;

@AutoConfiguration
@ConditionalOnProperty(
    prefix = "drednote.telegram.update-handler",
    name = "scenario-enabled",
    havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties({ScenarioProperties.class})
@ConditionalOnBean(ScenarioConfigurer.class)
public class ScenarioAutoConfiguration {

    @AutoConfiguration
    public static class DatasourceScenarioAutoConfiguration {

        @AutoConfiguration
        @ConditionalOnClass(MongoRepository.class)
        public static class MongoScenarioAutoConfiguration {

            @Bean
            @ConditionalOnMissingBean(ScenarioIdRepositoryAdapter.class)
            @ConditionalOnBean(MongoScenarioIdRepository.class)
            public ScenarioIdRepositoryAdapter scenarioIdRepositoryAdapter(
                MongoScenarioIdRepository scenarioIdRepository
            ) {
                return new MongoScenarioIdRepositoryAdapter(scenarioIdRepository);
            }

            @Bean
            @ConditionalOnMissingBean(ScenarioRepositoryAdapter.class)
            @ConditionalOnBean(MongoScenarioRepository.class)
            public <S> ScenarioRepositoryAdapter<S> scenarioRepositoryAdapter(MongoScenarioRepository repository) {
                return new MongoScenarioRepositoryAdapter<>(repository);
            }
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
    public static ScenarioFactoryBeanPostProcessor scenarioFactoryBeanPostProcessor(ScenarioFactoryContainer container) {
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
        ScenarioConfigurer<S> adapter, ScenarioProperties scenarioProperties,
        @Autowired(required = false) @Nullable ScenarioIdRepositoryAdapter scenarioIdAdapter,
        @Autowired(required = false) @Nullable ScenarioRepositoryAdapter<S> scenarioRepositoryAdapter,
        ScenarioFactoryResolver scenarioFactoryResolver
    ) throws Exception {
        ScenarioBuilder<S> builder = new ScenarioBuilder<>(ScenarioStateMachineBuilder.builder());

        ScenarioPropertiesConfigurer<S> propertiesConfigurer = new ScenarioPropertiesConfigurer<>(
            builder, scenarioProperties, scenarioFactoryResolver);
        propertiesConfigurer.collectStates();

        adapter.onConfigure(new DefaultScenarioConfigConfigurer<>(builder));
        adapter.onConfigure(new DefaultScenarioTransitionConfigurer<>(builder));
        adapter.onConfigure(new DefaultScenarioStateConfigurer<>(builder));

        propertiesConfigurer.configure();

        ScenarioData<S> data = builder.build();

        ScenarioIdResolver resolver;
        if (data.resolver() == null) {
            resolver = scenarioIdAdapter == null
                ? new DefaultScenarioIdResolver(new InMemoryScenarioIdRepositoryAdapter())
                : new DefaultScenarioIdResolver(scenarioIdAdapter);
        } else {
            resolver = data.resolver();
        }

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