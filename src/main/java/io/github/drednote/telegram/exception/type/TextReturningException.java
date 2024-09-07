package io.github.drednote.telegram.exception.type;

/**
 * Exception that return text to client.
 */
public class TextReturningException extends TelegramException {

    public TextReturningException(String message) {
        super(message);
    }

    public TextReturningException(String message, Throwable cause) {
        super(message, cause);
    }

    public TextReturningException(Throwable cause) {
        super(cause);
    }
}
