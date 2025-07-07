package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.choice.DefaultScenarioChoiceTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.choice.DefaultScenarioJunctionTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.choice.ScenarioChoiceTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.choice.ScenarioJunctionTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.DefaultScenarioEntryTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.DefaultScenarioExitTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.DefaultScenarioForkTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.DefaultScenarioHistoryTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.DefaultScenarioJoinTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.ScenarioEntryTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.ScenarioExitTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.ScenarioForkTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.ScenarioHistoryTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo.ScenarioJoinTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

public class DefaultScenarioTransitionConfigurer<S> implements ScenarioTransitionConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final StateMachineTransitionConfigurer<S, ScenarioEvent> configurer;

    public DefaultScenarioTransitionConfigurer(ScenarioBuilder<S> builder) {
        this.builder = builder;
        this.configurer = builder.configureTransitions();
    }

    public DefaultScenarioTransitionConfigurer(
        ScenarioBuilder<S> builder,
        StateMachineTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioExternalTransitionConfigurer<S> withExternal() throws Exception {
        return new DefaultScenarioExternalTransitionConfigurer<>(builder, configurer.withExternal());
    }

    @Override
    public ScenarioRollbackTransitionConfigurer<S> withRollback() throws Exception {
        return new DefaultScenarioRollbackTransitionConfigurer<>(builder, configurer.withExternal());
    }

    @Override
    public ScenarioInternalTransitionConfigurer<S> withInternal() throws Exception {
        return new DefaultScenarioInternalTransitionConfigurer<>(builder, configurer.withInternal());
    }

    @Override
    public ScenarioLocalTransitionConfigurer<S> withLocal() throws Exception {
        return new DefaultScenarioLocalTransitionConfigurer<>(builder, configurer.withLocal());
    }

    @Override
    public ScenarioChoiceTransitionConfigurer<S> withChoice() throws Exception {
        return new DefaultScenarioChoiceTransitionConfigurer<>(builder, configurer.withChoice());
    }

    @Override
    public ScenarioJunctionTransitionConfigurer<S> withJunction() throws Exception {
        return new DefaultScenarioJunctionTransitionConfigurer<>(builder, configurer.withJunction());
    }

    @Override
    public ScenarioForkTransitionConfigurer<S> withFork() throws Exception {
        return new DefaultScenarioForkTransitionConfigurer<>(builder, configurer.withFork());
    }

    @Override
    public ScenarioJoinTransitionConfigurer<S> withJoin() throws Exception {
        return new DefaultScenarioJoinTransitionConfigurer<>(builder, configurer.withJoin());
    }

    @Override
    public ScenarioEntryTransitionConfigurer<S> withEntry() throws Exception {
        return new DefaultScenarioEntryTransitionConfigurer<>(builder, configurer.withEntry());
    }

    @Override
    public ScenarioExitTransitionConfigurer<S> withExit() throws Exception {
        return new DefaultScenarioExitTransitionConfigurer<>(builder, configurer.withExit());
    }

    @Override
    public ScenarioHistoryTransitionConfigurer<S> withHistory() throws Exception {
        return new DefaultScenarioHistoryTransitionConfigurer<>(builder, configurer.withHistory());
    }
}
