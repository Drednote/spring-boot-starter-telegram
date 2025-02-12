package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.Getter;

public class UserScenarioContext {
    public boolean isEnd = false;
    public String nextScenario;
    @Getter
    private UpdateRequest updateRequest;

    //@Getter
    // private JSONObject data;

    public UserScenarioContext(UpdateRequest updateRequest, String data) {
        //this.data = data != null ? new JSONObject(data) : new JSONObject();
        this.updateRequest = updateRequest;
    }

}
