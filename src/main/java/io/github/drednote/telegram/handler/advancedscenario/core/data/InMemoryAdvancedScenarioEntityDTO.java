package io.github.drednote.telegram.handler.advancedscenario.core.data;

import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedActiveScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InMemoryAdvancedScenarioEntityDTO implements IAdvancedScenarioEntity {
    private Long userId;
    private Long chatId;
    private Instant changeDate = Instant.now();
    private Optional<ArrayList<IAdvancedActiveScenarioEntity>> activeScenarios;
    private Optional<String> data;
}
