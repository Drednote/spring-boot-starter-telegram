package io.github.drednote.telegram.handler.scenario.factory;

import io.github.drednote.telegram.handler.scenario.DefaultScenario;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import org.springframework.statemachine.config.StateMachineFactory;

/**
 * Factory for creating {@link Scenario} instances based on pre-configured state machines.
 * <p>
 * The {@code MachineScenarioFactory} implements the {@link ScenarioFactory} interface, providing methods to generate
 * scenario objects with specified identifiers. It utilizes a {@link StateMachineFactory} to obtain a state machine, and
 * incorporates persistence and ID resolution mechanisms to manage scenario data integrity.
 * </p>
 * <p>
 * When creating a scenario, it initializes a {@link DefaultScenario} with the state machine corresponding to the given
 * identifier, along with persistence and ID resolution components.
 * </p>
 *
 * @param <S> the type of state managed within the scenario
 * @author Ivan Galushko
 */
public class MachineScenarioFactory<S> implements ScenarioFactory<S> {

    private final ScenarioPersister<S> scenarioPersister;
    private final StateMachineFactory<S, ScenarioEvent> machineFactory;
    private final ScenarioIdResolver scenarioIdResolver;

    /**
     * Constructs a new {@code MachineScenarioFactory} with the specified components.
     *
     * @param machineFactory     factory for creating state machine instances
     * @param scenarioPersister  persister for scenario data
     * @param scenarioIdResolver resolver for scenario identifiers
     */
    public MachineScenarioFactory(
        StateMachineFactory<S, ScenarioEvent> machineFactory, ScenarioPersister<S> scenarioPersister,
        ScenarioIdResolver scenarioIdResolver
    ) {
        this.scenarioPersister = scenarioPersister;
        this.machineFactory = machineFactory;
        this.scenarioIdResolver = scenarioIdResolver;
    }

    /**
     * Creates a new scenario instance with the given identifier.
     * <p>
     * Uses the factory to obtain a state machine matching the scenario ID and wraps it into a {@link DefaultScenario},
     * injecting persistence and ID resolution components.
     * </p>
     *
     * @param scenarioId the unique identifier for the scenario
     * @return a new {@code Scenario} instance
     */
    @Override
    public Scenario<S> create(String scenarioId) {
        return new DefaultScenario<>(machineFactory.getStateMachine(scenarioId), scenarioPersister, scenarioIdResolver);
    }
}
