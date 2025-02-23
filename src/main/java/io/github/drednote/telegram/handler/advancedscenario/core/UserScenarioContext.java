package io.github.drednote.telegram.handler.advancedscenario.core;

import io.github.drednote.telegram.core.request.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.util.PathMatcher;

import java.util.Map;

@Getter
public class UserScenarioContext {
    private final UpdateRequest updateRequest;

    @Setter
    @NonNull
    private TransitionContext transitionContext = new TransitionContext();

    @Setter
    private Exception exception; //exception that thrown during scenario processing

    @Setter
    private Boolean isFinished;
    @Setter
    private TelegramRequest telegramRequest;


    /**
     * Retrieves the template variables associated with the action context.
     * <p>
     * Variables are extracting by {@link PathMatcher}.
     * <p>
     * Example of configuring transition if you want extract variables:
     * <pre>{@code
     *  telegramRequest(callbackQuery("Variable={value:.*}"))
     * }</pre>
     * If the message is "Variable=foo", then you will get a map {@code value=foo}
     *
     * @return a map of template variable names to their corresponding values
     */
    @Setter
    Map<String, String> variables;

    @NonNull
    private final JSONObject data;

    public UserScenarioContext(UpdateRequest updateRequest, String data) {
        this.data = data != null ? new JSONObject(data) : new JSONObject();
        this.updateRequest = updateRequest;
    }

}
