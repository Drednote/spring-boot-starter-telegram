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

    public UserScenarioContext(UpdateRequest updateRequest, Optional<String> data) {
        this.data = data.isPresent() ? new JSONObject(data) : new JSONObject();
        this.updateRequest = updateRequest;
    }

}
