package io.github.drednote.telegram.exception.type;

public class SessionTelegramException extends TelegramException {

    public SessionTelegramException(String message) {
        super(message);
    }

    public SessionTelegramException(String message, Throwable cause) {
        super(message, cause);
    }
}
