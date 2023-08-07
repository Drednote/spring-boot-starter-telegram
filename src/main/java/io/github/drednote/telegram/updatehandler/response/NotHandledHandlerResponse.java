package io.github.drednote.telegram.updatehandler.response;

public final class NotHandledHandlerResponse extends SimpleMessageHandlerResponse {

  public static final NotHandledHandlerResponse INSTANCE = new NotHandledHandlerResponse();

  private NotHandledHandlerResponse() {
    super("response.notHandled", "Unknown command or text, try something else");
  }
}
