package io.github.drednote.telegram.handler.scenario.configurer.transition.choice;

import static io.github.drednote.telegram.handler.scenario.DefaultScenario.INLINE_KEYBOARD_PROPERTY;

import io.github.drednote.telegram.handler.scenario.action.Action;
import io.github.drednote.telegram.handler.scenario.action.DelegateAction;
import io.github.drednote.telegram.handler.scenario.action.DelegatePropertiesAction;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.DefaultScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.guard.DelegateGuard;
import io.github.drednote.telegram.handler.scenario.guard.Guard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.statemachine.config.configurers.JunctionTransitionConfigurer;

public class DefaultScenarioJunctionTransitionConfigurer<S> implements ScenarioJunctionTransitionConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final JunctionTransitionConfigurer<S, ScenarioEvent> configurer;
    protected final Map<String, Object> props = new HashMap<>();

    private S source;
    private ChoiceData<S> first;
    private final List<ChoiceData<S>> then = new ArrayList<>();
    private ChoiceData<S> last;
    private boolean inlineKeyboard = false;

    public DefaultScenarioJunctionTransitionConfigurer(
        ScenarioBuilder<S> builder, JunctionTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> source(S source) {
        this.source = source;
        return this;
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> first(S target, Guard<S> guard) {
        return this.first(target, guard, null, null);
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> first(S target, Guard<S> guard, Action<S> action) {
        return this.first(target, guard, action, null);
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> first(S target, Guard<S> guard, Action<S> action, Action<S> error) {
        this.first = new ChoiceData<>(source, target, guard, action, error);
        return this;
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> then(S target, Guard<S> guard) {
        return this.then(target, guard, null, null);
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> then(S target, Guard<S> guard, Action<S> action) {
        return this.then(target, guard, action, null);
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> then(S target, Guard<S> guard, Action<S> action, Action<S> error) {
        this.then.add(new ChoiceData<>(source, target, guard, action, error));
        return this;
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> last(S target) {
        return this.last(target, null, null);
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> last(S target, Action<S> action) {
        return this.last(target, action, null);
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> last(S target, Action<S> action, Action<S> error) {
        this.last = new ChoiceData<>(source, target, null, action, error);
        return this;
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> props(Map<String, Object> props) {
        this.props.putAll(props);
        return this;
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> inlineKeyboardCreation() {
        this.inlineKeyboard = true;
        return this;
    }

    @Override
    public ScenarioTransitionConfigurer<S> and() throws Exception {
        if (source != null) {
            configurer.source(source);
        }

        Map<String, Object> properties = new HashMap<>();

        if (inlineKeyboard) {
            properties.put(INLINE_KEYBOARD_PROPERTY, true);
        }

        if (first != null) {
            configurer.first(first.target(),
                first.guard() != null ? new DelegateGuard<>(first.guard(), props) : null,
                first.action() != null ? new DelegatePropertiesAction<>(first.action(), props, properties) : null,
                first.error() != null ? new DelegateAction<>(first.error(), props) : null);
        }

        for (ChoiceData<S> data : then) {
            configurer.then(data.target(),
                data.guard() != null ? new DelegateGuard<>(data.guard(), props) : null,
                data.action() != null ? new DelegatePropertiesAction<>(data.action(), props, properties) : null,
                data.error() != null ? new DelegateAction<>(data.error(), props) : null);
        }

        if (last != null) {
            configurer.last(last.target(),
                last.action() != null ? new DelegatePropertiesAction<>(last.action(), props, properties) : null,
                last.error() != null ? new DelegateAction<>(last.error(), props) : null);
        }

        return new DefaultScenarioTransitionConfigurer<>(builder, configurer.and());
    }
}
