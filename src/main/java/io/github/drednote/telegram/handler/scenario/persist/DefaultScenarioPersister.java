package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext.DefaultScenarioContext;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.AbstractStateMachinePersister;

public class DefaultScenarioPersister<S>
    extends AbstractStateMachinePersister<S, ScenarioEvent, String>
    implements ScenarioPersister<S> {

    private final ScenarioRepositoryAdapter<S> adapter;

    /**
     * Instantiates a new scenario persister.
     */
    public DefaultScenarioPersister(ScenarioRepositoryAdapter<S> adapter) {
        super(new UnsupportedStateMachinePersist<>());
        this.adapter = adapter;
    }

    @Override
    public void persist(Scenario<S> context) throws Exception {
        var machineContext = buildStateMachineContext(context.getStateMachine());
        adapter.save(new DefaultScenarioContext<>(context.getId(), machineContext));
    }

    @Override
    public Scenario<S> restore(Scenario<S> scenario, String scenarioId) {
        adapter.findById(scenarioId).ifPresent(context -> scenario.getAccessor().resetScenario(context));
        return scenario;
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
