package io.github.drednote.telegram.updatehandler;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;

public interface UpdateHandler {

  void onUpdate(TelegramUpdateRequest request) throws Exception;

}
