package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.handler.scenario.MachineScenario;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
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
        return new MachineScenario<>(machineFactory.getStateMachine(scenarioId), scenarioPersister, scenarioIdResolver);
    }
}
