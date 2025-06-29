package io.github.drednote.telegram.handler.scenario.factory;

import io.github.drednote.telegram.handler.scenario.DefaultScenario;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import org.springframework.statemachine.config.StateMachineFactory;

public class MachineScenarioFactory<S> implements ScenarioFactory<S> {

    private final ScenarioPersister<S> scenarioPersister;
    private final StateMachineFactory<S, ScenarioEvent> machineFactory;
    private final ScenarioIdResolver scenarioIdResolver;

    public MachineScenarioFactory(
        StateMachineFactory<S, ScenarioEvent> machineFactory, ScenarioPersister<S> scenarioPersister,
        ScenarioIdResolver scenarioIdResolver
    ) {
        this.scenarioPersister = scenarioPersister;
        this.machineFactory = machineFactory;
        this.scenarioIdResolver = scenarioIdResolver;
    }

    @Override
    public Scenario<S> create(String scenarioId) {
        return new DefaultScenario<>(machineFactory.getStateMachine(scenarioId), scenarioPersister, scenarioIdResolver);
    }
}
