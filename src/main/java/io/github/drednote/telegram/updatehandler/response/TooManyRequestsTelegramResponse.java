package io.github.drednote.telegram.updatehandler.response;

public final class TooManyRequestsTelegramResponse extends SimpleMessageTelegramResponse {

  public static final TooManyRequestsTelegramResponse INSTANCE = new TooManyRequestsTelegramResponse();

  private TooManyRequestsTelegramResponse() {
    super("response.tooManyRequests", "Too many requests. Please try later");
  }
}
