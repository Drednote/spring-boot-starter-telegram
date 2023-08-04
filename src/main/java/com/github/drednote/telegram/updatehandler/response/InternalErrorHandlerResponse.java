package com.github.drednote.telegram.updatehandler.response;

public final class InternalErrorHandlerResponse extends SimpleMessageHandlerResponse {

  public static final InternalErrorHandlerResponse INSTANCE = new InternalErrorHandlerResponse();

  private InternalErrorHandlerResponse() {
    super("response.internalError", "Oops, something went wrong, please try again later.");
  }
}
