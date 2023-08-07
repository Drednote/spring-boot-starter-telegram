package io.github.drednote.telegram.updatehandler.response;

public final class InternalErrorTelegramResponse extends SimpleMessageTelegramResponse {

  public static final InternalErrorTelegramResponse INSTANCE = new InternalErrorTelegramResponse();

  private InternalErrorTelegramResponse() {
    super("response.internalError", "Oops, something went wrong, please try again later.");
  }
}
