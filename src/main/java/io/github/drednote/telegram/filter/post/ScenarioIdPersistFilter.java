package io.github.drednote.telegram.filter.post;

import static io.github.drednote.telegram.handler.scenario.DefaultScenario.INLINE_KEYBOARD_PROPERTY;
import static io.github.drednote.telegram.handler.scenario.DefaultScenario.SUCCESS_EXECUTION_PROPERTY;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.ScenarioAccessor;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioExternalTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.factory.ScenarioIdResolver;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;

/**
 * {@code ScenarioIdPersistFilter} is a filter that persists the scenario associated with {@code UpdateRequest} and
 * processes the response messages from the telegram if necessary.
 *
 * @author Ivan Galushko
 * @see Scenario
 * @see ScenarioExternalTransitionConfigurer#inlineKeyboardCreation()
 */
public class ScenarioIdPersistFilter implements ConclusivePostUpdateFilter {

    private static final Logger log = LoggerFactory.getLogger(ScenarioIdPersistFilter.class);

    /**
     * Processes the post-filter step after an update request is made. It persists the scenario associated with
     * {@code UpdateRequest} and processes the response messages from the telegram if necessary.
     *
     * @param request the {@code UpdateRequest} containing the details of the update along with the responses.
     */
    @Override
    public void postFilter(@NonNull UpdateRequest request) throws Exception {
        List<Object> responses = request.getResponseFromTelegram();
        Scenario<?> scenario = request.getScenario();
        if (scenario != null && Boolean.TRUE.equals(scenario.getProperty(SUCCESS_EXECUTION_PROPERTY))) {
            if (Boolean.TRUE.equals(scenario.getProperty(INLINE_KEYBOARD_PROPERTY))) {
                if (!responses.isEmpty()) {
                    log.warn(
                        "No response received from telegram, although response message processing "
                        + "for scenario getMachine is enabled. Scenario Id = '{}'", scenario.getId());
                }
                for (Object response : responses) {
                    if (response instanceof MaybeInaccessibleMessage message) {
                        String messageId = ScenarioIdResolver.resolveId(request, message);
                        scenario.getAccessor().setId(messageId);
                        break;
                    }
                }
            }
            persist(scenario);
        }
    }

    /**
     * Persists the provided scenario using its accessor's persister.
     *
     * @param scenario the {@code Scenario} object that needs to be persisted.
     * @param <T>      the type parameter for the scenario.
     */
    private <T> void persist(Scenario<T> scenario) throws Exception {
        ScenarioAccessor<T> accessor = scenario.getAccessor();
        accessor.getPersister().persist(scenario);
    }

    /**
     * Returns the post-order value for this filter.
     *
     * @return the integer value representing the post-order of this filter in the filter chain.
     */
    @Override
    public int getPostOrder() {
        return FilterOrder.CONCLUSIVE_POST_FILTERS.get(this.getClass());
    }
}
