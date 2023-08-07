package io.github.drednote.telegram.updatehandler.response;

public final class ForbiddenTelegramResponse extends SimpleMessageTelegramResponse {

  public static final ForbiddenTelegramResponse INSTANCE = new ForbiddenTelegramResponse();

  private ForbiddenTelegramResponse() {
    super("response.forbidden", "You do not have access to this bot!");
  }
}
