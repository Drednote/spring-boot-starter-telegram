package io.github.drednote.telegram.handler.advancedscenario.core;

public class UserScenarioContext {
    public String input;
    public String response;
    public boolean isEnd = false;
    public String nextScenario;

    public UserScenarioContext() {}

    public UserScenarioContext(String input) {
        this.input = input;
    }
}
