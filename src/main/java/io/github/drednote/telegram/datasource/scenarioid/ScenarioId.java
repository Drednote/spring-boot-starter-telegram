package io.github.drednote.telegram.datasource.scenarioid;

public interface ScenarioId {

    String getId();

    String getScenarioId();

//    void setScenarioId(String scenarioId);

    record DefaultScenarioId(String id, String scenarioId) implements ScenarioId {

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getScenarioId() {
            return scenarioId;
        }
    }
}
