package io.github.drednote.telegram.handler.scenario;

public class WrappingException extends RuntimeException {

  public WrappingException(String message, Throwable cause) {
    super(message, cause);
  }
}
