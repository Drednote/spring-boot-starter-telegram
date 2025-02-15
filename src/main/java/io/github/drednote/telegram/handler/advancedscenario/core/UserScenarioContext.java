package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.JSONObject;

@Getter
public class UserScenarioContext{
    private final UpdateRequest updateRequest;

    @Setter
    ScenarioWithState<?> previosScenarioWithState;
    @Setter
    ScenarioWithState<?> nextScenarioWithState;

    @Setter
    private Exception exception; //exception that thrown during scenario processing

    @Setter
    private Boolean isFinished;
    @Setter
    private TelegramRequest telegramRequest;

    @NonNull
    private final JSONObject data;

    public UserScenarioContext(UpdateRequest updateRequest, String data) {
        this.data = data != null ? new JSONObject(data) : new JSONObject();
        this.updateRequest = updateRequest;
    }

}
