package io.github.drednote.telegram.exception.type;

public abstract class TelegramException extends RuntimeException {

  protected TelegramException(String message) {
    super(message);
  }

  protected TelegramException(String message, Throwable cause) {
    super(message, cause);
  }

  protected TelegramException(Throwable cause) {
    super(cause);
  }
}
