package io.github.drednote.telegram.handler.scenario.configurer.transition;

import static io.github.drednote.telegram.handler.scenario.machine.ScenarioProperties.RESPONSE_PROCESSING_KEY;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioEvent;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioProperties;
import io.github.drednote.telegram.handler.scenario.machine.ScenarioPropertiesAction;
import java.util.Map;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;

public class SimpleScenarioResponseMessageTransitionConfigurer<S>
    extends
    AbstractScenarioBaseTransitionConfigurer<ExternalTransitionConfigurer<S, ScenarioEvent>, ScenarioResponseMessageTransitionConfigurer<S>, S>
    implements ScenarioResponseMessageTransitionConfigurer<S> {

    private final ExternalTransitionConfigurer<S, ScenarioEvent> configurer;

    public SimpleScenarioResponseMessageTransitionConfigurer(
        ScenarioBuilder<S> builder,
        ExternalTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        super(builder);
        this.configurer = configurer;
    }

    @Override
    public ScenarioResponseMessageTransitionConfigurer<S> target(S target) {
        this.target = target;
        return this;
    }

    protected StateMachineTransitionConfigurer<S, ScenarioEvent> build() throws Exception {
        preBuild(configurer);

        if (target != null) {
            configurer.target(target);
        }

        ScenarioPropertiesAction<S> action = new ScenarioPropertiesAction<>(
            new ScenarioProperties(Map.of(RESPONSE_PROCESSING_KEY, true)));
        configurer.action(action);

        return configurer.and();
    }
}
