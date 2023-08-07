package io.github.drednote.telegram.updatehandler.response;

public final class NotHandledTelegramResponse extends SimpleMessageTelegramResponse {

  public static final NotHandledTelegramResponse INSTANCE = new NotHandledTelegramResponse();

  private NotHandledTelegramResponse() {
    super("response.notHandled", "Unknown command or text, try something else");
  }
}
