package io.github.drednote.telegram.datasource.scenario;

public abstract class ScenarioEntity {

    public abstract String getId();

    public abstract String getState();

    public abstract byte[] getContext();
}
