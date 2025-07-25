package io.github.drednote.telegram.core.request;

import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.handler.controller.RequestHandler;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.response.CompositeTelegramResponse;
import io.github.drednote.telegram.response.TelegramResponse;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * The {@code DefaultUpdateRequest} class is default implementation of the {@link UpdateRequest} interface
 *
 * @author Ivan Galushko
 */
@Getter
public class DefaultUpdateRequest extends AbstractUpdateRequest {

    private final TelegramClient telegramClient;
    private final List<Object> responseFromTelegram = new ArrayList<>();

    @Nullable
    private Permission permission;

    @Nullable
    private RequestHandler requestHandler;

    @Nullable
    private Scenario<?> scenario;

    @Setter
    @Nullable
    private TelegramResponse response;

    /**
     * If error occurred during update handling
     */
    @Setter
    @Nullable
    private Throwable error;

    /**
     * Creates a new instance of the DefaultUpdateRequest class with the given parameters.
     *
     * @param update     the update received from Telegram.
     * @param telegramClient  the abstract sender used to send responses.
     */
    public DefaultUpdateRequest(
        Update update, TelegramClient telegramClient
    ) {
        super(update);
        Assert.required(telegramClient, "TelegramClient");

        this.telegramClient = telegramClient;
    }

    /**
     * Create new instance of {@code DefaultUpdateRequest} class based on an existing {@code UpdateRequest}
     *
     * @param request existing {@code UpdateRequest}
     */
    public DefaultUpdateRequest(UpdateRequest request) {
        super(request);
        Assert.required(request, "UpdateRequest");

        this.telegramClient = request.getTelegramClient();
        this.requestHandler = request.getRequestHandler();
        this.scenario = request.getScenario();
        this.response = request.getResponse();
        this.error = request.getError();
        this.permission = request.getPermission();
    }

    @Override
    public void addResponse(TelegramResponse response) {
        Assert.notNull(response, "Response");
        if (this.response == null) {
            this.response = response;
        } else {
            this.response = new CompositeTelegramResponse(this.response, response);
        }
    }

    @Override
    public String toString() {
        return "Id = %s, text = %s".formatted(this.id, this.text);
    }

    @Override
    public void setScenario(@Nullable Scenario<?> scenario) {
        if (this.scenario == null) {
            this.scenario = scenario;
        }
    }

    @Override
    public void setRequestHandler(@Nullable RequestHandler requestHandler) {
        if (this.requestHandler == null) {
            this.requestHandler = requestHandler;
        }
    }

    @Override
    public void setPermission(@Nullable Permission permission) {
        if (this.permission == null) {
            this.permission = permission;
        }
    }

    @Override
    public void addResponseFromTelegram(Object response) {
        this.responseFromTelegram.add(response);
    }
}
