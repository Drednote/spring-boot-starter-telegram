package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEventResult;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEventResult.DefaultScenarioEventResult;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioContext;
import io.github.drednote.telegram.handler.scenario.persist.ScenarioPersister;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.StateMachineEventResult.ResultType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DefaultScenario<S> implements Scenario<S>, ScenarioAccessor<S> {

    public static final String RESPONSE_PROCESSING_PROPERTY = "responseMessageProcessing";
    public static final String SUCCESS_EXECUTION_PROPERTY = "successExecution";

    private final StateMachine<S, ScenarioEvent> machine;
    private final ScenarioPersister<S> scenarioPersister;
    private final ScenarioIdResolver scenarioIdResolver;
    private final Map<String, Object> properties = new HashMap<>();
    private final Lock lock = new ReentrantLock();

    private String id;

    public DefaultScenario(
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
    public StateMachine<S, ScenarioEvent> getStateMachine() {
        return machine;
    }

    @Override
    public ScenarioEventResult<S, ScenarioEvent> sendEvent(UpdateRequest request) {
        try {
            lock.lock();
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

            addProperty(SUCCESS_EXECUTION_PROPERTY, accepted.get());

            return new DefaultScenarioEventResult<>(accepted.get(), results, request.getError());
        } catch (Exception e) {
            return new DefaultScenarioEventResult<>(false, null, e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean matches(UpdateRequest request) {
        ScenarioEvent event = new ScenarioEvent(request);
        return machine.getTransitions().stream()
            .filter(t -> t.getSource().getId().equals(machine.getState().getId()))
            .anyMatch(t -> t.getTrigger().getEvent().equals(event));
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
    public void addProperty(String key, @Nullable Object value) {
        if (value != null) {
            this.properties.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getProperty(String key) {
        Object object = properties.get(key);
        if (object != null) {
            return (T) object;
        }
        return null;
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
