package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.core.UpdateRequest;

public interface UpdateHandler {

  void onUpdate(UpdateRequest update);

}
