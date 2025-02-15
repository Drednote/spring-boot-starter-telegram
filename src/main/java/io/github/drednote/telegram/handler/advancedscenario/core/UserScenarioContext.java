package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.Optional;

@Getter
public class UserScenarioContext {
    private UpdateRequest updateRequest;

    @Setter
    private Boolean isFinished;
    @Setter
    private TelegramRequest telegramRequest;
    private JSONObject data;

    public UserScenarioContext(UpdateRequest updateRequest, String data) {
        this.data = data != null ? new JSONObject(data) : null;
        this.updateRequest = updateRequest;
    }

}
