package io.github.drednote.telegram.handler.scenario.property;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

@ConfigurationProperties("drednote.telegram.scenario")
@Getter
@Setter
public class ScenarioProperties {

    @Nullable
    private Map<String, Scenario> values;
    @Nullable
    private Rollback defaultRollback;

    @Getter
    @Setter
    public static class Scenario {

        private Request request;
        private Set<String> actionReferences;
        private TransitionType type = TransitionType.External;
        private String source;
        private String target;
        /**
         * Only if type == Rollback.
         */
        @Nullable
        private Rollback rollback;

        private Map<String, Object> props = new HashMap<>();
        private List<Scenario> children = new ArrayList<>();

        public enum TransitionType {
            Rollback, ResponseMessageProcessing, External
        }
    }

    @Getter
    @Setter
    public static class Request {

        private Set<String> patterns;
        private Set<RequestType> requestTypes;
        private Set<MessageType> messageTypes = new HashSet<>();
        private boolean exclusiveMessageType = false;
    }

    @Getter
    @Setter
    public static class Rollback {

        private Request request;
        private Set<String> actionReferences;
    }
}
