package io.github.drednote.telegram.handler.scenario.configurer.transition.pseudo;

import io.github.drednote.telegram.handler.scenario.configurer.ScenarioBuilder;
import io.github.drednote.telegram.handler.scenario.configurer.transition.DefaultScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.event.ScenarioEvent;
import java.util.ArrayList;
import java.util.List;
import org.springframework.statemachine.config.configurers.ForkTransitionConfigurer;

public class DefaultScenarioForkTransitionConfigurer<S>
    extends AbstractScenarioPseudoTransitionConfigurer<S>
    implements ScenarioForkTransitionConfigurer<S> {

    private final ScenarioBuilder<S> builder;
    private final ForkTransitionConfigurer<S, ScenarioEvent> configurer;

    private final List<S> targets = new ArrayList<>();

    public DefaultScenarioForkTransitionConfigurer(
        ScenarioBuilder<S> builder,
        ForkTransitionConfigurer<S, ScenarioEvent> configurer
    ) {
        super(builder);
        this.builder = builder;
        this.configurer = configurer;
    }

    @Override
    public ScenarioPseudoTransitionConfigurer<S> target(S target) {
        targets.add(target);
        return this;
    }

    @Override
    public ScenarioTransitionConfigurer<S> and() throws Exception {
        if (source != null) {
            configurer.source(source);
        }
        for (S s : targets) {
            configurer.target(s);
        }

        return new DefaultScenarioTransitionConfigurer<>(builder, configurer.and());
    }
}
