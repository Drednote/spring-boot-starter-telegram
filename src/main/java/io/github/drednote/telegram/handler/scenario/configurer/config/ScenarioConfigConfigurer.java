package io.github.drednote.telegram.handler.scenario.configurer.config;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.factory.DefaultScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;

/**
 * Interface for configuring general scenario settings.
 *
 * @param <S> the type of the state
 * @author Ivan Galushko
 */
public interface ScenarioConfigConfigurer<S> {

    /**
     * Sets the persister for the scenario configuration.
     *
     * @param persister the ScenarioPersister to use for persisting scenarios
     * @return the current instance of ScenarioConfigConfigurer
     */
    ScenarioConfigConfigurer<S> withPersister(ScenarioPersister<S> persister);

    /**
     * Sets the id resolver for the scenario configuration.
     * <p>
     * {@code ScenarioIdResolver} serves to determine by which ID to try to find the scenario for each
     * {@link UpdateRequest}. It can be userId for example, or something else.
     * <p>
     * By default, used {@link DefaultScenarioIdResolver}, but if you want manually control the behaviour you can set
     * yours {@code ScenarioIdResolver}
     *
     * @param resolver the ScenarioIdResolver to use for resolving scenario IDs
     * @return the current instance of ScenarioConfigConfigurer
     */
    ScenarioConfigConfigurer<S> withIdResolver(ScenarioIdResolver resolver);

    /**
     * Gets a configurer for generic config.
     *
     * @return {@link ScenarioConfigurationConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioConfigurationConfigurer<S> withConfiguration() throws Exception;

    /**
     * Gets a configurer for distributed state machine config.
     *
     * @return {@link ScenarioDistributedConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioDistributedConfigurer<S> withDistributed() throws Exception;

    /**
     * Gets a configurer for state machine model verifier.
     *
     * @return {@link ScenarioVerifierConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ScenarioVerifierConfigurer<S> withVerifier() throws Exception;

//    /**
//     * Gets a configurer for state machine monitoring.
//     *
//     * @return {@link ScenarioMonitoringConfigurer} for chaining
//     * @throws Exception if configuration error happens
//     */
//    ScenarioMonitoringConfigurer<S> withMonitoring() throws Exception;
}
