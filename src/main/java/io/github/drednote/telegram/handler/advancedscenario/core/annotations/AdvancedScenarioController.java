package io.github.drednote.telegram.handler.advancedscenario.core.annotations;

import io.github.drednote.telegram.core.annotation.BetaApi;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@BetaApi
public @interface AdvancedScenarioController {
    String name();
}
