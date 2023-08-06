package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;

public interface UpdateHandler {

  void onUpdate(ExtendedTelegramUpdateRequest request) throws Exception;

}
