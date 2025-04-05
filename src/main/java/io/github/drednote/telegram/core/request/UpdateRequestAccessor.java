package io.github.drednote.telegram.core.request;

import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioManager;
import io.github.drednote.telegram.handler.controller.RequestHandler;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.ScenarioUpdateHandler;
import io.github.drednote.telegram.response.TelegramResponse;
import java.io.Serializable;
import org.springframework.lang.Nullable;

public interface UpdateRequestAccessor {


    /**
     * Sets the scenario associated with the request
     *
     * @param scenario the scenario to set
     * @apiNote Can only be set once. If a scenario is already set, do nothing
     * @see ScenarioUpdateHandler
     */
    void setScenario(@Nullable Scenario<?> scenario);

    /**
     * Sets the scenario advanced manager associated with the request
     *
     * @param advancedScenarioManager the scenario to set
     * @apiNote Can only be set once. If a scenario is already set, do nothing
     * @see ScenarioUpdateHandler
     */
    void setAdvancedScenarioManager(@Nullable AdvancedScenarioManager advancedScenarioManager);

    /**
     * Sets the response that should be sent to Telegram
     *
     * @param response the response to set
     * @see ResponseSetter
     */
    void setResponse(@Nullable TelegramResponse response);

    /**
     * Sets the info for invocation {@link TelegramRequest} methods
     *
     * @param requestHandler the info for invocation
     * @apiNote Can only be set once. If info is already set, do nothing
     */
    void setRequestHandler(@Nullable RequestHandler requestHandler);

    /**
     * Sets the permission of the user executing the request
     *
     * @param permission the permission to set
     * @apiNote Can only be set once. If permission is already set, do nothing
     */
    void setPermission(@Nullable Permission permission);

    /**
     * Adds the response from telegram after sending response to telegram
     *
     * @param response the response from telegram
     */
    void addResponseFromTelegram(Serializable response);
}
