package io.github.drednote.telegram.handler.scenario.machine;

import io.github.drednote.telegram.utils.Assert;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.lang.Nullable;

public class ScenarioState<S> {

    private final S value;
    private final Map<String, Object> properties = new HashMap<>();

    public ScenarioState(S value, @Nullable Map<String, Object> properties) {
        Assert.required(value, "value");
        this.value = value;
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }

    public ScenarioState(S value) {
        this(value, null);
    }

    @Nullable
    public <T> T getProperty(String key) {
        return (T) properties.get(key);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScenarioState<?> that = (ScenarioState<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
