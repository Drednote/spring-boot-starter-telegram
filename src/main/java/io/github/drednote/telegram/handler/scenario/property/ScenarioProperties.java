package io.github.drednote.telegram.handler.scenario.property;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioExternalTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioRollbackTransitionConfigurer;
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

/**
 * This class holds configuration properties for scenarios.
 * <p>
 * It includes various properties related to scenarios, requests, and rollbacks.
 *
 * @author Ivan Galushko
 */
@ConfigurationProperties("drednote.telegram.scenario")
@Getter
@Setter
@BetaApi
public class ScenarioProperties {

    /**
     * A map of scenario names to their corresponding {@link Scenario} objects.
     */
    @Nullable
    private Map<String, Scenario> values;

    /**
     * The default rollback configuration, which applies if no another set in scenario object.
     */
    @Nullable
    private Rollback defaultRollback;

    /**
     * Represents a scenario with its associated properties and actions.
     */
    @Getter
    @Setter
    public static class Scenario {

        /**
         * The request associated with this scenario.
         */
        private Request request;
        /**
         * A set of action references names related to this scenario. The name must be exactly the same as in
         * {@link TelegramScenarioAction}.
         */
        private Set<String> actionReferences;
        /**
         * The type of transition for this scenario. Defaults to {@link TransitionType#EXTERNAL}.
         */
        private TransitionType type = TransitionType.EXTERNAL;
        /**
         * The source state identifier for this scenario.
         */
        private String source;
        /**
         * The target state identifier for this scenario.
         */
        private String target;
        /**
         * A list of nodes representing the graph of this scenario. The graph is explaining the connections between
         * different scenarios. The path from one scenario to others. ID is the same as a key in {@link #steps}
         * <p>
         * Example:
         * <pre>{@code   graph:
         *     - id: get
         *     - id: change
         *       children:
         *         - id: lang-change}
         *  </pre>
         */
        private List<Node> graph = new ArrayList<>();
        /**
         * The rollback configuration, applicable only if the type is {@link TransitionType#ROLLBACK}.
         */
        @Nullable
        private Rollback rollback;
        /**
         * A map of additional properties related to this scenario. This props pass to the {@link ActionContext}.
         */
        private Map<String, Object> props = new HashMap<>();
        /**
         * A map of steps associated with this scenario, where each step is another scenario. The keys are the same as
         * {@link Node#id} in {@link #graph}.
         */
        private Map<String, Scenario> steps = new HashMap<>();

        /**
         * Enum representing the types of transitions for a scenario.
         */
        public enum TransitionType {
            /**
             * The type associated with a {@link ScenarioRollbackTransitionConfigurer}
             */
            ROLLBACK,
            /**
             * The type associated with a {@link ScenarioExternalTransitionConfigurer} with parameter
             * {@link ScenarioExternalTransitionConfigurer#inlineKeyboardCreation()} set to true
             */
            RESPONSE_MESSAGE_PROCESSING,
            /**
             * The type associated with a {@link ScenarioExternalTransitionConfigurer}
             */
            EXTERNAL
        }
    }

    /**
     * Represents a node in the scenario graph.
     */
    @Getter
    @Setter
    public static class Node {

        /**
         * The unique identifier for this node.
         */
        private String id;
        /**
         * A list of child nodes for this node.
         */
        private List<Node> children = new ArrayList<>();
    }

    /**
     * Represents a request configuration for a scenario.
     */
    @Getter
    @Setter
    public static class Request {

        /**
         * @see TelegramRequest#pattern()
         */
        private Set<String> patterns;
        /**
         * @see TelegramRequest#requestType()
         */
        private Set<RequestType> requestTypes;
        /**
         * @see TelegramRequest#messageType()
         */
        private Set<MessageType> messageTypes = new HashSet<>();
        /**
         * @see TelegramRequest#exclusiveMessageType()
         */
        private boolean exclusiveMessageType = false;
    }

    /**
     * Represents a rollback configuration for a scenario.
     */
    @Getter
    @Setter
    public static class Rollback {

        /**
         * The request associated with the rollback.
         */
        private Request request;
        /**
         * @see Scenario#actionReferences
         */
        private Set<String> actionReferences;
    }
}
