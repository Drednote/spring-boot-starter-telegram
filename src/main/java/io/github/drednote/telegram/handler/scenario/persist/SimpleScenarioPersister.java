package io.github.drednote.telegram.handler.scenario.persist;

import io.github.drednote.telegram.datasource.scenario.ScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.utils.FieldProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleScenarioPersister<S> implements ScenarioPersister<S> {

    private final FieldProvider<? extends ScenarioRepositoryAdapter<S>> adapterProvider;

    @Override
    public void persist(Scenario<S> context) {
        try {
            adapterProvider.ifExistsWithException(adapter -> adapter.save(convert(context)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Scenario<S> restore(Scenario<S> scenario, String scenarioId) {
        return adapterProvider.toOptional()
            .flatMap(adapter -> adapter.findById(scenarioId))
            .map(context -> doRestore(context, scenario))
            .orElse(scenario);
    }

    private Scenario<S> doRestore(ScenarioContext<S> context, Scenario<S> scenario) {
        scenario.getAccessor().resetScenario(context);
        return scenario;
    }

    private ScenarioContext<S> convert(Scenario<S> scenario) {
        StateContext<S> stateContext = new SimpleStateContext<>(scenario.getState());
        List<SimpleTransitionContext<S>> transitionContexts = scenario.getTransitionsHistory().stream()
            .map(SimpleTransitionContext::new).toList();

        return new SimpleScenarioContext<>(scenario.getId(), stateContext, transitionContexts);
    }


}
