package io.github.drednote.telegram.datasource.scenarioid;

/**
 * Provides a link between the user and the currently active scenario.
 * <p>
 * Used to determine which {@code scenarioId} is currently assigned to the user (or other entity), identified by
 * {@code id}. At the same time, {@code scenarioId} can change during the interaction, reflecting a change in context,
 * stage or business flow.
 *
 * <p>For example, if the user goes through a sequence of steps (registration scenario, order placement, etc.),
 * then this entity allows you to understand which scenarios he is currently in.</p>
 *
 * <p>As a rule, they are used by storages and routing logic in Telegram bots.</p>
 *
 * @author Ivan Galushko
 */
public interface ScenarioId {

    /**
     * Returns a unique identifier for the user or other entity that the scenario is attached to.
     *
     * @return a unique identifier (e.g. chatId)
     */
    String getId();

    /**
     * Returns the identifier of the scenario associated with the entity.
     *
     * @return the scenario ID
     */
    String getScenarioId();

    /**
     * Default immutable implementation of {@link ScenarioId} using Java record.
     *
     * @param id         the entity identifier (e.g. chatId)
     * @param scenarioId the scenario identifier
     */
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
