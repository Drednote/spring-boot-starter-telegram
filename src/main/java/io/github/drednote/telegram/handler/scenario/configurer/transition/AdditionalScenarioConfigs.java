package io.github.drednote.telegram.handler.scenario.configurer.transition;

import io.github.drednote.telegram.handler.scenario.action.Action;
import io.github.drednote.telegram.handler.scenario.action.ActionContext;
import java.util.Map;

public interface AdditionalScenarioConfigs<S, C extends ScenarioTransitionConfigurerBuilder<S>> {

    /**
     * Sets the additional props to be used during the transition.
     *
     * @param props additional props to pass to {@link Action} in {@link ActionContext}
     * @return the current instance of the configurer
     */
    C props(Map<String, Object> props);

    /**
     * After execution of this transition and processing response message to user, id of the current scenario will be
     * changed to messageId of a response message from a telegram.
     * <p>
     * Use this method if you during transition creating a message with inline keyboard, and want to interact with this
     * message independently of other scenarios.
     */
    C inlineKeyboardCreation();
}
