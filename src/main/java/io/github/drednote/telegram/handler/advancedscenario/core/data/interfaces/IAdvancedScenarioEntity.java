package io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces;

import lombok.Getter;

import java.time.Instant;

public interface IAdvancedScenarioEntity {
    String getUserId();
    String getChatId();
    Instant getChangeDate();
    String getScenarioName();
    String getStatusName();
    String getData();

    default String getKey() {
        return getUserId() + ":" + getChatId(); // Composite key
    }
}
