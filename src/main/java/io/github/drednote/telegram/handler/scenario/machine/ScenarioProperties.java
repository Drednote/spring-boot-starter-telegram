package io.github.drednote.telegram.handler.scenario.machine;

import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;

public class ScenarioProperties {

    public static final String RESPONSE_PROCESSING_KEY = "responseMessageProcessing";

    private final Map<String, Object> properties;

    public ScenarioProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public ScenarioProperties() {
        this.properties = new HashMap<>();
    }

    public void addProperties(@Nullable ScenarioProperties properties) {
        if (properties != null) {
            this.properties.putAll(properties.properties);
        }
    }

    @Nullable
    public <T> T getProperty(String key) {
        Object object = properties.get(key);
        if (object != null) {
            return (T) object;
        }
        return null;
    }
}
