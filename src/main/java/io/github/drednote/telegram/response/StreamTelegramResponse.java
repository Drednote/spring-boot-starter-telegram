package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StreamTelegramResponse extends AbstractTelegramResponse {

    private final Stream<?> response;
    @Nullable
    private TelegramApiException exception = null;

    public StreamTelegramResponse(Stream<?> response) {
        Assert.required(response, "response");
        this.response = response;
    }

    @Override
    public void process(UpdateRequest request) throws TelegramApiException {
        try {
            response.forEach(o -> {
                    if (o != null) {
                        try {
                            new GenericTelegramResponse(o).process(request);
                            this.exception = null;
                        } catch (TelegramApiException e) {
                            if (exception == null) {
                                this.exception = e;
                            } else {
                                throw new StreamException(e);
                            }
                        }
                    }
                });
            if (exception != null) {
                throw exception;
            }
        } catch (StreamException e) {
            throw e.exception;
        }
    }

    private static class StreamException extends RuntimeException {

        TelegramApiException exception;

        public StreamException(TelegramApiException exception) {
            this.exception = exception;
        }
    }
}
