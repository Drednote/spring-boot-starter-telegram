package io.github.drednote.telegram.exception.type;

/**
 * Exception that throws during scenario processing.
 */
public class ScenarioException extends TelegramException {

    public ScenarioException(String message) {
        super(message);
    }

    public ScenarioException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScenarioException(Throwable cause) {
        super(cause);
    }
}
