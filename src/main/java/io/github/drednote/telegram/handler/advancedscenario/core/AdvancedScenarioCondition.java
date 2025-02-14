package io.github.drednote.telegram.handler.advancedscenario.core;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class AdvancedScenarioCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getRegistry().containsBeanDefinition("advancedScenarioStorage");
    }
}
