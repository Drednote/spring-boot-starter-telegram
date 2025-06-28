package io.github.drednote.telegram.handler.scenario.configurer;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.handler.scenario.Action;
import io.github.drednote.telegram.handler.scenario.machine.DelegateAction;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.statemachine.config.configurers.StateConfigurer.History;

public class DefaultStateConfigurer<S> implements StateConfigurer<S> {

    private final org.springframework.statemachine.config.configurers.StateConfigurer<S, ScenarioEvent> configurer;
    private final ScenarioBuilder<S> builder;

    public DefaultStateConfigurer(
        ScenarioBuilder<S> builder,
        org.springframework.statemachine.config.configurers.StateConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public StateConfigurer<S> initial(S initial) {
        configurer.initial(makeState(initial));
        builder.setInitialState(makeState(initial));
        return this;
    }

    @Override
    public StateConfigurer<S> initial(S initial, Action<S> action) {
        configurer.initial(makeState(initial), makeAction(action));
        builder.setInitialState(makeState(initial));
        return this;
    }

    @Override
    public StateConfigurer<S> parent(S state) {
        configurer.parent(makeState(state));
        return this;
    }

    @Override
    public StateConfigurer<S> region(String id) {
        configurer.region(id);
        return this;
    }

    @Override
    public StateConfigurer<S> state(S state) {
        configurer.state(makeState(state));
        return this;
    }

    @Override
    public StateConfigurer<S> state(S state, Collection<? extends Action<S>> stateActions) {
        var actions = stateActions.stream().map(this::makeAction).toList();
        configurer.state(makeState(state), actions);
        return this;
    }

    @Override
    public StateConfigurer<S> state(S state, Action<S> stateAction) {
        configurer.state(makeState(state), makeAction(stateAction));
        return this;
    }

    @Override
    public StateConfigurer<S> stateDo(S state, Action<S> action) {
        configurer.stateDo(makeState(state), makeAction(action));
        return this;
    }

    @Override
    public StateConfigurer<S> stateDo(S state, Action<S> action, Action<S> error) {
        configurer.stateDo(makeState(state), makeAction(action), makeAction(error));
        return this;
    }

    @Override
    public StateConfigurer<S> state(S state, Collection<? extends Action<S>> entryActions,
        Collection<? extends Action<S>> exitActions) {
        var actions = entryActions.stream().map(this::makeAction).toList();
        var exit = exitActions.stream().map(this::makeAction).toList();
        configurer.state(makeState(state), actions, exit);
        return this;
    }

    @Override
    public StateConfigurer<S> state(S state, Action<S> entryAction, Action<S> exitAction) {
        configurer.state(makeState(state), makeAction(entryAction), makeAction(exitAction));
        return this;
    }

    @Override
    public StateConfigurer<S> stateEntry(S state, Action<S> action) {
        configurer.stateEntry(makeState(state), makeAction(action));
        return this;
    }

    @Override
    public StateConfigurer<S> stateEntry(S state, Action<S> action, Action<S> error) {
        configurer.stateEntry(makeState(state), makeAction(action), makeAction(error));
        return this;
    }

    @Override
    public StateConfigurer<S> stateExit(S state, Action<S> action) {
        configurer.stateExit(makeState(state), makeAction(action));
        return this;
    }

    @Override
    public StateConfigurer<S> stateExit(S state, Action<S> action, Action<S> error) {
        configurer.stateExit(makeState(state), makeAction(action), makeAction(error));
        return this;
    }

    @Override
    public StateConfigurer<S> state(S state, TelegramRequest... deferred) {
        if (deferred.length > 0) {
            ScenarioEvent[] events = Arrays.stream(deferred).map(ScenarioEvent::new).toArray(ScenarioEvent[]::new);
            configurer.state(makeState(state), events);
        }
        return this;
    }

    @Override
    public StateConfigurer<S> states(Set<S> states) {
        configurer.states(states.stream().map(this::makeState).collect(Collectors.toSet()));
        return this;
    }

    @Override
    public StateConfigurer<S> end(S end) {
        configurer.end(makeState(end));
        return this;
    }

    @Override
    public StateConfigurer<S> choice(S choice) {
        configurer.choice(makeState(choice));
        return this;
    }

    @Override
    public StateConfigurer<S> junction(S junction) {
        configurer.junction(makeState(junction));
        return this;
    }

    @Override
    public StateConfigurer<S> fork(S fork) {
        configurer.fork(makeState(fork));
        return this;
    }

    @Override
    public StateConfigurer<S> join(S join) {
        configurer.join(makeState(join));
        return this;
    }

    @Override
    public StateConfigurer<S> history(S history, History type) {
        configurer.history(makeState(history), type);
        return this;
    }

    @Override
    public StateConfigurer<S> entry(S entry) {
        configurer.entry(makeState(entry));
        return this;
    }

    @Override
    public StateConfigurer<S> exit(S exit) {
        configurer.exit(makeState(exit));
        return this;
    }

//    @Override
//    public ScenarioStateConfigurer<S> and() {
//        return new SimpleScenarioStateConfigurer<>(builder, configurer.and());
//    }

    private org.springframework.statemachine.action.Action<S, ScenarioEvent> makeAction(
        Action<S> action
    ) {
        return new DelegateAction<>(action, null);
    }

    private S makeState(S state) {
        return state;
    }
}
