package io.github.drednote.telegram.handler.scenario.machine;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext.SimpleScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.AbstractStateMachinePersister;

public class MachineScenarioPersister<S>
    extends AbstractStateMachinePersister<S, ScenarioEvent, String>
    implements ScenarioPersister<S> {

    private final ScenarioRepositoryAdapter<S> adapter;

    /**
     * Instantiates a new scenario persister.
     */
    public MachineScenarioPersister(ScenarioRepositoryAdapter<S> adapter) {
        super(new UnsupportedStateMachinePersist<>());
        this.adapter = adapter;
    }

    @Override
    public void persist(Scenario<S> context) throws Exception {
        StateMachineContext<S, ScenarioEvent> machineContext = buildStateMachineContext(
            context.getStateMachine());
        adapter.save(new SimpleScenarioContext<>(context.getId(), machineContext));
    }

    @Override
    public void restore(Scenario<S> scenario, String scenarioId) {
        adapter.findById(scenarioId).ifPresent(context -> scenario.getAccessor().resetScenario(context));
    }

    private static final class UnsupportedStateMachinePersist<S> implements StateMachinePersist<S, ScenarioEvent, String> {

        @Override
        public void write(StateMachineContext<S, ScenarioEvent> context, String contextObj) {
            throw new UnsupportedOperationException("Not support write statemachine");
        }

        @Override
        public StateMachineContext<S, ScenarioEvent> read(String contextObj) {
            throw new UnsupportedOperationException("Not support read statemachine");
        }
    }
}
