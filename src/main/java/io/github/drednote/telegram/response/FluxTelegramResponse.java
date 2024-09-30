package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Flux;

public class FluxTelegramResponse extends AbstractTelegramResponse {

    private final Flux<Object> response;
    @Nullable
    private TelegramApiException exception = null;

    public FluxTelegramResponse(Object response) {
        Assert.required(response, "response");
        if (!(response instanceof Flux)) {
            throw new IllegalArgumentException("This class work only with a Flux");
        }
        this.response = (Flux<Object>) response;
    }

    @Override
    public void process(UpdateRequest request) throws TelegramApiException {
        try {
            response.doOnNext(o -> {
                    if (o != null) {
                        try {
                            new GenericTelegramResponse(o).process(request);
                            this.exception = null;
                        } catch (TelegramApiException e) {
                            if (exception == null) {
                                this.exception = e;
                            } else {
                                throw new FluxException(e);
                            }
                        }
                    }
                })
                .blockLast();
            if (exception != null) {
                throw exception;
            }
        } catch (FluxException e) {
            throw e.exception;
        }
    }

    private static class FluxException extends RuntimeException {

        TelegramApiException exception;

        public FluxException(TelegramApiException exception) {
            this.exception = exception;
        }
    }
}
