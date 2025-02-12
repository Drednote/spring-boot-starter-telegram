package io.github.drednote.telegram.handler.advancedscenario.core.data;

import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedScenarioEntityDTO implements IAdvancedScenarioEntity {
    private String userId;
    private String chatId;
    private Instant changeDate = Instant.now();
    private String scenarioName;
    private String statusName;
    private String data;
}
