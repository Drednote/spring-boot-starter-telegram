package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.Getter;

import java.util.Optional;

@Getter
public class UserScenarioContext {
    private UpdateRequest updateRequest;

    //@Getter
    // private JSONObject data;

    public UserScenarioContext(UpdateRequest updateRequest, Optional<String> data) {
        //this.data = data != null ? new JSONObject(data) : new JSONObject();
        this.updateRequest = updateRequest;
    }

}
