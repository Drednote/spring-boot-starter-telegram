package io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IAdvancedScenarioEntity {
    String getUserId();

    String getChatId();

    Instant getChangeDate();

    Optional<List<IAdvancedActiveScenarioEntity>> getActiveScenarios();

    void setActiveScenarios(Optional<List<IAdvancedActiveScenarioEntity>> activeScenarios);

    Optional<String> getData();

    void setData(Optional<String> data);

    default String getKey() {
        return getUserId() + ":" + getChatId(); // Composite key
    }
}
