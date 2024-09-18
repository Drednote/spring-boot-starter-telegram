package io.github.drednote.telegram.handler.scenario.configurer.transition;

public interface ScenarioTransitionConfigurer<S> {

    ScenarioExternalTransitionConfigurer<S> withExternal();

    ScenarioInlineMessageTransitionConfigurer<S> withCreateInlineMessage();

    ScenarioRollbackTransitionConfigurer<S> withRollback();
}
