package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.core.request.ExtendedBotRequest;

public interface UpdateHandler {

  void onUpdate(ExtendedBotRequest request) throws Exception;

}
