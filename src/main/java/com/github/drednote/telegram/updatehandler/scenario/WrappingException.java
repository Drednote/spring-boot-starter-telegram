package com.github.drednote.telegram.updatehandler.scenario;

public class WrappingException extends RuntimeException {

  public WrappingException(String message, Throwable cause) {
    super(message, cause);
  }
}
