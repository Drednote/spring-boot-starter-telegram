package io.github.drednote.telegram.updatehandler;

import io.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;

public interface UpdateHandler {

  void onUpdate(ExtendedTelegramUpdateRequest request) throws Exception;

}
