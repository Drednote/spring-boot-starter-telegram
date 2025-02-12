package io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces;

import java.util.Optional;

public interface IAdvancedScenarioStorage {
    void save(IAdvancedScenarioEntity entity); // Save a record
    Optional<IAdvancedScenarioEntity> findById(String key); // Find by composite key
    void deleteById(String key); // Delete by composite key
    int size(); // Number of records
}
