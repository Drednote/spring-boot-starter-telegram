package io.github.drednote.telegram.exception.type;

public abstract class RequestValidationException extends TextReturningException {

  protected RequestValidationException(String message) {
    super(message);
  }

  protected RequestValidationException(Throwable cause) {
    super(cause);
  }

  protected RequestValidationException(String message, Throwable cause) {
    super(message, cause);
  }

}
