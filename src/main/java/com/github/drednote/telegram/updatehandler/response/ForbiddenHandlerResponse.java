package com.github.drednote.telegram.updatehandler.response;

public final class ForbiddenHandlerResponse extends SimpleMessageHandlerResponse {

  public static final ForbiddenHandlerResponse INSTANCE = new ForbiddenHandlerResponse();

  private ForbiddenHandlerResponse() {
    super("response.forbidden", "You do not have access to this bot!");
  }
}
