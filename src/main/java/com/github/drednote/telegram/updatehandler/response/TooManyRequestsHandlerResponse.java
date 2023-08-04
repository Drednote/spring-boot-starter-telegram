package com.github.drednote.telegram.updatehandler.response;

public final class TooManyRequestsHandlerResponse extends SimpleMessageHandlerResponse {

  public static final TooManyRequestsHandlerResponse INSTANCE = new TooManyRequestsHandlerResponse();

  private TooManyRequestsHandlerResponse() {
    super("response.tooManyRequests", "Too many requests. Please try later");
  }
}
