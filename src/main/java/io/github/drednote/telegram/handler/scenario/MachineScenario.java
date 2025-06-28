package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.ScenarioEventResult.SimpleScenarioEventResult;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioProperties;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.StateMachineEventResult.ResultType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MachineScenario<S> implements Scenario<S>, ScenarioAccessor<S> {

    private final StateMachine<S, ScenarioEvent> machine;
    private final ScenarioPersister<S> scenarioPersister;
    private final ScenarioIdResolver scenarioIdResolver;
    private final ScenarioProperties properties = new ScenarioProperties();
    private String id;

    public MachineScenario(
        StateMachine<S, ScenarioEvent> machine, ScenarioPersister<S> scenarioPersister,
        ScenarioIdResolver scenarioIdResolver
    ) {
        this.machine = machine;
        this.scenarioPersister = scenarioPersister;
        this.scenarioIdResolver = scenarioIdResolver;
        this.id = machine.getId();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    @Nullable
    public <T> T getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public StateMachine<S, ScenarioEvent> getStateMachine() {
        return machine;
    }

    @Override
    public ScenarioEventResult<S, ScenarioEvent> sendEvent(UpdateRequest request) {
        try {
            Message<ScenarioEvent> message = new GenericMessage<>(new ScenarioEvent(request));

            List<StateMachineEventResult<S, ScenarioEvent>> results = machine
                .sendEvent(Mono.just(message))
                .switchIfEmpty(Flux.just(StateMachineEventResult.from(machine, message, ResultType.DENIED)))
                .toStream()
                .toList();

            AtomicBoolean accepted = new AtomicBoolean(true);
            results.forEach(result -> {
                if (result.getResultType() == ResultType.DENIED) {
                    accepted.set(false);
                }
            });

            return new SimpleScenarioEventResult<>(accepted.get(), results, request.getError());
        } catch (Exception e) {
            return new SimpleScenarioEventResult<>(false, null, e);
        }
    }

    @Override
    public boolean matches(UpdateRequest request) {
        return machine.getTransitions().stream()
            .filter(t -> t.getSource().getId().equals(machine.getState().getId()))
            .anyMatch(t -> t.getTrigger().getEvent().equals(new ScenarioEvent(request)));
    }

    @Override
    public boolean isTerminated() {
        return machine.isComplete();
    }

    @Override
    public ScenarioAccessor<S> getAccessor() {
        return this;
    }

    @Override
    public void resetScenario(ScenarioContext<S> context) {
        machine.stopReactively().block();
        machine.getStateMachineAccessor()
            .doWithAllRegions(function -> function.resetStateMachineReactively(context.getMachine()).block());
        machine.startReactively().block();
        this.id = context.getId();
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void addProperties(ScenarioProperties properties) {
        this.properties.addProperties(properties);
    }

    @Override
    public ScenarioIdResolver getIdResolver() {
        return scenarioIdResolver;
    }

    @Override
    public ScenarioPersister<S> getPersister() {
        return scenarioPersister;
    }
}
