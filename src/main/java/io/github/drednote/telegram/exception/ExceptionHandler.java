package io.github.drednote.telegram.exception;

import io.github.drednote.telegram.core.request.UpdateRequest;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

/**
 * The {@code ExceptionHandler} interface describes a contract for classes that handle exceptions that may occur during
 * the processing of a UpdateRequest
 *
 * @author Ivan Galushko
 */
public interface ExceptionHandler {

    /**
     * Handles exceptions that occur during the processing of a {@code UpdateRequest}
     *
     * @param request the {@code UpdateRequest} object representing the request to be processed, not null
     */
    void handle(@NonNull UpdateRequest request);

    default Mono<Void> handleReactive(UpdateRequest request) {
        return Mono.fromRunnable(() -> handle(request));
    }
}
